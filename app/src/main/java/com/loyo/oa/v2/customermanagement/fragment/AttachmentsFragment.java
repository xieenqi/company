package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.adapter.CustomerAttachmentsListAdapter;
import com.loyo.oa.v2.customermanagement.cell.AttachmentCell;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
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

public class AttachmentsFragment extends CustomerChildFragment
        implements UploadControllerCallback, AttachmentCell.OnAttachmentCellListener,
        PullToRefreshBase.OnRefreshListener2 {

    /**
     * Crash fix, not set id to work around
     * java.lang.IllegalArgumentException: Wrong state class, expecting View State but
     * received class android.support.v7.widget.RecyclerView$SavedState instead
     */
    //@BindView(R.id.file_list_view)
    PullToRefreshRecyclerView2 listView;
    @BindView(R.id.file_add) ViewGroup layout_add;
    @BindView(R.id.file_loading) LoadingLayout ll_loading;

    View view;
    String uuid;
    int bizType = 6;
    boolean canAdd;

    private ArrayList<Attachment> mListAttachment;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    CustomerAttachmentsListAdapter adapter;

    private UploadController controller;

    public AttachmentsFragment() {
        this.title = "附件";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(
                    R.layout.fragment_attachments, container, false);

            ButterKnife.bind(this, view);
            controller = new UploadController(getActivity(), 9);
            controller.setObserver(this);
            adapter = new CustomerAttachmentsListAdapter(this);

            initViews(view);
            if (uuid != null) {
                getAttachments();
            }
        }
        return view;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setCustomer(Customer customer) {
        this.uuid = customer.uuid;
        this.totalCount = customer.counter.getFile();
        this.canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                        customer.state, CustomerAction.ATTACHMENT_ADD);
    }

    void initViews(View view) {
        ButterKnife.bind(this, view);
        listView = (PullToRefreshRecyclerView2) view.findViewWithTag("file_list");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getAttachments();
            }
        });
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnTouchListener(Global.GetTouch());

        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getContext()));
        listView.getRefreshableView().setAdapter(adapter);
        listView.setOnRefreshListener(this);
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
                        ll_loading.setStatus(LoadingLayout.Success);
                        listView.onRefreshComplete();
                        mListAttachment = attachments;
                        adapter.loadData(mListAttachment);
                    }
                });
    }

    /**
     * 附件上传
     */
    @OnClick(R.id.file_add)
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

    @Override
    public void onAttachmentSelected(int index) {
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < mListAttachment.size(); i++) {
            String path = mListAttachment.get(i).getUrl();
            if (path != null) {
                selectedPhotos.add(path);
            }
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(false)
                .start(this.getActivity(), this);
    }

    /**
     * onPullDownToRefresh will be called only when the user has Pulled from
     * the start, and released.
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        getAttachments();
    }

    /**
     * onPullUpToRefresh will be called only when the user has Pulled from
     * the end, and released.
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
