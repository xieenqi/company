package com.loyo.oa.v2.activityui.attachment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;


/**
 * 附件列表 【添加附件】页面
 */

@EActivity(R.layout.activity_attachment)
public class AttachmentActivity extends BaseActivity implements UploadControllerCallback {

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
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();

    UploadController controller;

    @AfterViews
    void init() {
        super.setTitle("附件");

        controller = new UploadController(this, 9);
        controller.setObserver(this);

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
        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(ll_loading) {
                    public void onError(Throwable e) {
                        @LoyoErrorChecker.CheckType
                        int type = ListUtil.IsEmpty(mListAttachment)?
                                LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                    }
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        mListAttachment = attachments;
                        bindAttachment();
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
            adapter = new AttachmentSwipeAdapter(mContext, mListAttachment,
                    mUserList, mListViewAttachment, bizType, uuid, isOver);
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
//        Intent intent = new Intent(this, SelectPicPopupWindow.class);
//        intent.putExtra("localpic", true);
//        intent.putExtra("addpg", false);
//        startActivityForResult(intent, PhotoPicker.REQUEST_CODE);

        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this);
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
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    if (mSelectPath.size() > 0) {
                        showCommitLoading();
                        controller.startUpload();
                    }
                }

                break;

            default:
                break;

        }
    }

//    /**
//     * 批量上传附件
//     */
//    private void newUploadAttachement(File file) {
//        uploadSize++;
//        showCommitLoading();
//        TypedFile typedFile = new TypedFile("image/*", file);
//        TypedString typedUuid = new TypedString(uuid);
//        AttachmentService.newUpload(typedUuid, bizType, typedFile)
//                .subscribe(new DefaultLoyoSubscriber<Attachment>(hud) {
//                    @Override
//                    public void onNext(Attachment attachment) {
//                        getAttachments();
//                    }
//                });
//    }

    private void buildAttachment() {
        ArrayList<UploadTask> list = controller.getTaskList();
        attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = bizType;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        buildAttachment();
        AttachmentService.setAttachementData(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<AttachmentForNew>>(hud) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        controller.removeAllTask();
                    }

                    @Override
                    public void onNext(ArrayList<AttachmentForNew> news) {
                        getAttachments();
                        controller.removeAllTask();
                    }
                });
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {

    }

    @Override
    public void onAddEvent(UploadController controller) {

    }

    @Override
    public void onItemSelected(UploadController controller, int index) {

    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
        // TODO: 上传失败提醒
        if (taskList.size() >0) {
            postAttaData();
        }
    }
}
