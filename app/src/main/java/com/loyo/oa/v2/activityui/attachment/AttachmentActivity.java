package com.loyo.oa.v2.activityui.attachment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;


/**
 * 附件列表 【添加附件】页面
 */

@EActivity(R.layout.activity_attachment)
public class AttachmentActivity extends BaseActivity {

    @Extra("users")
    ArrayList<User> mUserList;

    @Extra("uuid")
    String uuid;

    @Extra("bizType")
    int bizType;

    @Extra("isOver")
    boolean isOver; //当前业务已经结束

    @Extra("canAdd")
    boolean canAdd;

    @Extra("fromPage")
    int fromPage;

    @ViewById(R.id.listView_attachment)
    SwipeListView mListViewAttachment;
    @ViewById(R.id.tv_upload)
    TextView tv_upload;
    @ViewById
    LoadingLayout ll_loading;

    private ArrayList<Attachment> mListAttachment;
    private AttachmentSwipeAdapter adapter;
    private int uploadSize;
    private int uploadNum;

    @AfterViews
    void init() {
        super.setTitle("附件");
        if (fromPage == Common.CUSTOMER_PAGE) {
            tv_upload.setVisibility( canAdd? View.VISIBLE: View.GONE);
        } else {
            if (isOver) {
                tv_upload.setVisibility(View.GONE);
            }
        }
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getAttachments();
            }
        });
        getAttachments();
    }

    /**
     * 获取附件列表信息
     */
    @UiThread
    void getAttachments() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                mListAttachment = attachments;
                bindAttachment();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error,ll_loading);
                finish();
            }
        });
    }

    /**
     * 绑定附件
     */
    void bindAttachment() {
        if (ListUtil.IsEmpty(mListAttachment)) {
            ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }

        Attachment.Sort(mListAttachment);
        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mContext, mListAttachment, mUserList,mListViewAttachment, bizType, uuid, isOver);
            adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
                @Override
                public void afterDelete(final Attachment attachment) {
                    //附件删除后重新绑定
                    mListAttachment.remove(attachment);
                    //                bindAttachment();
                    //不能重新绑定，会报错，只需要通知adapter即可 ykb 07-23
                    adapter.notifyDataSetChanged();
                }
            });
            mListViewAttachment.setSwipeCloseAllItemsWhenMoveList(true);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
            adapter.notifyDataSetChanged();
        }
        if (uploadNum == uploadSize) {
            DialogHelp.cancelLoading();
        }
        adapter.refreshData();
        ll_loading.setStatus(LoadingLayout.Success);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", mListAttachment);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    /**
     * 返回
     */
    @Click(R.id.img_title_left)
    void click() {
        onBackPressed();
    }

    /**
     * 附件上传
     */
    @Click(R.id.tv_upload)
    void addAttachment() {
        Intent intent = new Intent(this, SelectPicPopupWindow.class);
        intent.putExtra("localpic", true);
        intent.putExtra("addpg", false);
        startActivityForResult(intent, MainApp.GET_IMG);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case AttachmentSwipeAdapter.REQUEST_ATTACHMENT:
                Attachment attachment = (Attachment) data.getSerializableExtra("data");
                if (null != attachment) {
                    for (int i = 0; i < mListAttachment.size(); i++) {
                        Attachment tempAttachment = mListAttachment.get(i);
                        if (TextUtils.equals(attachment.getId(), tempAttachment.getId())) {
                            mListAttachment.set(i, attachment);
                            ((AttachmentSwipeAdapter) mListViewAttachment.getAdapter()).notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;

            //附件上传回调
            case MainApp.GET_IMG:
                try {
                    ArrayList<ImageInfo> pickPhots = (ArrayList<ImageInfo>) data.getSerializableExtra("data");
                    if (pickPhots == null) {
                        return;
                    }
                    uploadSize = 0;
                    uploadNum = pickPhots.size();
                    for (ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);
                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                newUploadAttachement(newFile);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;

            default:
                break;

        }
    }

    /**
     * 批量上传附件
     */
    private void newUploadAttachement(File file) {
        if (uploadSize == 0) {
            DialogHelp.showLoading(mContext, "正在上传", true);
        }
        uploadSize++;
        TypedFile typedFile = new TypedFile("image/*", file);
        TypedString typedUuid = new TypedString(uuid);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                new RCallback<Attachment>() {
                    @Override
                    public void success(final Attachment attachments, final Response response) {
                        HttpErrorCheck.checkResponse(response);
                        getAttachments();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }
}
