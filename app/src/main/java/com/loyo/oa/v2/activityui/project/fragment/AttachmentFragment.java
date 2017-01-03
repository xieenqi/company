package com.loyo.oa.v2.activityui.project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :项目附件
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class AttachmentFragment extends BaseFragment implements View.OnClickListener, UploadControllerCallback {

    private View mView;
    private SwipeListView mListViewAttachment;
    private HttpProject mProject;
    private ArrayList<Attachment> mAttachments = new ArrayList<>();
    private AttachmentSwipeAdapter adapter;
    private ViewGroup layout_upload;
    private int bizType = 5;
    private boolean isOver;
    private LoadingLayout ll_loading;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    private UploadController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("project")) {
            mProject = (HttpProject) getArguments().getSerializable("project");
        }
        controller = new UploadController(this.getActivity(), 9);
        controller.setObserver(this);
    }

    /**
     * 显示图片
     *
     * @param mListAttachment
     */
    private void bindAttachment(final ArrayList<Attachment> mListAttachment) {
//        if (ListUtil.IsEmpty(mListAttachment)) {
//                ll_loading.setStatus(LoadingLayout.Empty);
//            return;
//        }
        onLoadSuccess(mListAttachment.size());
        final ArrayList<Attachment> sortAttachment = Attachment.Sort(mListAttachment);
        ArrayList<User> users = Common.getUsersByProject(mProject);

        if (mProject.status == 2) {
            isOver = true;
        }

        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mActivity, sortAttachment, users, mListViewAttachment, bizType, mProject.attachmentUUId, isOver);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
            adapter.setUsers(users);
            adapter.notifyDataSetChanged();
        }

        /*适配器回调*/
        adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
            @Override
            public void afterDelete(Attachment attachment) {
                onLoadSuccess(mListAttachment.size() - 1);
                mListAttachment.remove(attachment);
                adapter.setData(mListAttachment);
                adapter.notifyDataSetChanged();
            }
        });
        ll_loading.setStatus(LoadingLayout.Success);
    }

//    /**
//     * 检测权限
//     *
//     * @return
//     */
//    private boolean checkRights() {
//        boolean hasRights = false;
//        if (mProject.creator.equals(MainApp.user)) {
//            hasRights = true;
//        } else {
//            ArrayList<HttpProject.ProjectManaer> members = mProject.managers;
//            if (null != members && !members.isEmpty()) {
//                for (int i = 0; i < members.size(); i++) {
//                    if (members.get(i).user.equals(MainApp.user)) {
//                        hasRights = true;
//                        break;
//                    }
//                }
//            }
//        }
//        if (mProject.status == 2) {
//            return false;
//        }
//        return hasRights;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_upload:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this.getActivity(), this);

                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attachment, container, false);
            ll_loading = (LoadingLayout) mView.findViewById(R.id.ll_loading);
            ll_loading.setStatus(LoadingLayout.Loading);
            ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
                @Override
                public void onReload(View v) {
                    ll_loading.setStatus(LoadingLayout.Loading);
                    getData();
                }
            });
            mListViewAttachment = (SwipeListView) mView.findViewById(R.id.listView_attachment);
            layout_upload = (ViewGroup) mView.findViewById(R.id.layout_upload);
            layout_upload.setOnClickListener(this);
            getData();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mProject.isProjectRelevant()) {
            layout_upload.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        AttachmentService.getAttachments(mProject.attachmentUUId)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(ll_loading) {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        mAttachments = attachments;
                        bindAttachment(mAttachments);
                    }
                });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mProject != null && mProject.status == Project.STATUS_FINISHED) {
            layout_upload.setVisibility(View.GONE);
        } else {
            layout_upload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectChange(int status) {
        if (null != mProject) {
            mProject.status = status;
        }
        if (layout_upload == null) {
            return;
        }
        if (status == Project.STATUS_FINISHED) {
            layout_upload.setVisibility(View.GONE);
        } else {
            layout_upload.setVisibility(View.VISIBLE);
        }
    }


//    /**
//     * 批量上传附件
//     */
//    private void newUploadAttachement(File file) {
//        uploadSize++;
//        TypedFile typedFile = new TypedFile("image/*", file);
//        TypedString typedUuid = new TypedString(mProject.attachmentUUId);
//        AttachmentService.newUpload(typedUuid, bizType, typedFile)
//                .subscribe(new DefaultLoyoSubscriber<Attachment>() {
//                    @Override
//                    public void onNext(Attachment attachment) {
//                        if (mAttachments != null) {
//                            mAttachments.add(0, attachment);
//                        } else {
//                            mAttachments = new ArrayList<>(Arrays.asList(attachment));
//                        }
//                        bindAttachment(mAttachments);
//                    }
//                });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case AttachmentSwipeAdapter.REQUEST_ATTACHMENT:
                Attachment attachment = (Attachment) data.getSerializableExtra("data");
                if (null != attachment) {
                    for (int i = 0; i < mAttachments.size(); i++) {
                        Attachment tempAttachment = mAttachments.get(i);
                        if (TextUtils.equals(attachment.getId(), tempAttachment.getId())) {
                            mAttachments.set(i, attachment);
                            adapter.setData(mAttachments);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;

            /**
             * 附件上传回调
             * */
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    ArrayList<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, mProject.attachmentUUId);
                    }
                    if (mSelectPath.size() > 0) {
                        showCommitLoading();
                        controller.startUpload();
                    }
                }

                break;
        }
    }

    private void buildAttachment() {
        ArrayList<UploadTask> list = controller.getTaskList();
        attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = mProject.attachmentUUId;
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
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        controller.removeAllTask();
                    }

                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        if (mAttachments != null) {
                            mAttachments.addAll(news);
                        } else {
                            mAttachments = new ArrayList<>(news);
                        }
                        bindAttachment(mAttachments);

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
