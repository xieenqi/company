package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class AttachmentsFragment extends BaseFragment implements UploadControllerCallback {

    @BindView(R.id.listView_attachment) SwipeListView mListViewAttachment;
    @BindView(R.id.tv_upload) TextView tv_upload;
    @BindView(R.id.ll_loading) LoadingLayout ll_loading;

    View view;

    ArrayList<User> mUserList;
    String uuid;
    int bizType;
    boolean isOver; //当前业务已经结束
    boolean canAdd;
    int fromPage;

    private ArrayList<Attachment> mListAttachment;
    private AttachmentSwipeAdapter adapter;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();

    private UploadController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(
                    R.layout.fragment_attachments, container, false);

            ButterKnife.bind(this, view);
            loadIntentData();

            controller = new UploadController(getActivity(), 9);
            controller.setObserver(this);

            ll_loading.setStatus(LoadingLayout.Loading);
            ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
                @Override
                public void onReload(View v) {
                    ll_loading.setStatus(LoadingLayout.Loading);
                    getAttachments();
                }
            });
            if (uuid != null) {
                getAttachments();
            }
        }
        return view;
    }

    void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUserList = (ArrayList<User> ) bundle.getSerializable("users");
            uuid = bundle.getString("uuid");
            bizType = bundle.getInt("bizType");
            isOver = bundle.getBoolean("isOver", false);
            canAdd = bundle.getBoolean("canAdd", false);
            fromPage = bundle.getInt("fromPage");
        }
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 获取附件列表信息
     */
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
            adapter = new AttachmentSwipeAdapter(getActivity(), mListAttachment,
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

    /**
     * 附件上传
     */
    @OnClick(R.id.tv_upload)
    void addAttachment() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(getActivity(), this);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
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
