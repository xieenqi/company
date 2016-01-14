package com.loyo.oa.v2.activity.attachment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.swipelistview.SwipeListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 附件列表 【添加附件】页面
 */

@EActivity(R.layout.activity_attachment)
public class AttachmentActivity extends BaseActivity {

    @Extra("users")
    ArrayList<User> mUserList;

    @Extra("uuid")
    String uuid;

    @Extra("isMyUser")
    boolean isMyUser;

    @Extra("fromPage")
    int fromPage;

    @Extra("goneBtn")
    int goneBtn;

    @ViewById(R.id.listView_attachment)
    SwipeListView mListViewAttachment;
    @ViewById(R.id.tv_upload)
    TextView tv_upload;

    private ArrayList<Attachment> mListAttachment;
    private AttachmentSwipeAdapter adapter;

    @AfterViews
    void init() {
        super.setTitle("附件");

        if (!isMyUser) {
            tv_upload.setVisibility(View.GONE);
        }

        setTouchView(NO_SCROLL);
        getAttachments();

    }

    /**
     * 获取附件列表信息
     * */
    @UiThread
    void getAttachments() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> attachments, Response response) {
                mListAttachment = attachments;
                bindAttachment();
                HttpErrorCheck.checkResponse(response);
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                finish();
            }
        });
    }

    /**
     * 绑定附件
     */
    void bindAttachment() {
        if (ListUtil.IsEmpty(mListAttachment)) {
            return;
        }

        Attachment.Sort(mListAttachment);
        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mContext, mListAttachment, mUserList,goneBtn);
            adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
                @Override
                public void afterDelete(Attachment attachment) {
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
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", mListAttachment);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    /**返回*/
    @Click(R.id.img_title_left)
    void click() {
        finish();
    }

    /**附件上传*/
    @Click(R.id.tv_upload)
    void addAttachment() {
        Intent intent = new Intent(this, SelectPicPopupWindow.class);
        intent.putExtra("localpic", true);
        startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

            case SelectPicPopupWindow.GET_IMG:
                try {
                    ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
                    if (pickPhots == null) {
                        return;
                    }
                    for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(this, uri);
                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                uploadAttachment(newFile);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }
                break;
        }
    }

    /**
     * 上传附件
     */
    private void uploadAttachment(File file) {
        Utils.uploadAttachment(uuid, file)
                .subscribe(new CommonSubscriber(this) {
                    @Override
                    public void onNext(Serializable attachment) {
                        getAttachments();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Toast(e.getMessage());
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
