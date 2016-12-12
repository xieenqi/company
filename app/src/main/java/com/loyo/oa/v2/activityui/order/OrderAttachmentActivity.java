package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单附件】
 * Created by yyy on 16/8/2.
 */
public class OrderAttachmentActivity extends BaseLoadingActivity implements View.OnClickListener, UploadControllerCallback {

    private ArrayList<User> mUserList;
    private String uuid = StringUtil.getUUID();
    private int bizType;
    private int attachmentCount = 0; //当前附件总数
    private boolean isOver;         //当前业务已经结束
    private boolean isPic = false;
    private boolean isAdd;          //操作权限
    private boolean isCreat;//是否要创建附件

    private LinearLayout img_title_left;
    private TextView tv_title;
    private TextView tv_upload;
    private SwipeListView mListViewAttachment;

    private Intent mIntent;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    private ArrayList<Attachment> mListAttachment;
    private AttachmentSwipeAdapter adapter;
    private List<String> mSelectPath;

    UploadController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        initUI();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_order_attachment);
    }

    @Override
    public void getPageData() {
        getAttachments();
    }

    public void initUI() {
        mIntent = getIntent();
        if (null != mIntent) {
            mUserList = (ArrayList<User>) mIntent.getSerializableExtra("users");
            bizType = mIntent.getIntExtra("bizType", 0);
            isOver = mIntent.getBooleanExtra("isOver", false);
            isAdd = mIntent.getBooleanExtra(ExtraAndResult.EXTRA_ADD, true);
            if (null != mIntent.getStringExtra("uuid") && mIntent.getStringExtra("uuid").length() > 5) {
                uuid = mIntent.getStringExtra("uuid");
                isPic = true;
            }
            isCreat = bizType == 0 ? false : true;//biztype有值就是要创建上传附件的
        }

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title = (TextView) findViewById(R.id.tv_title_1);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        mListViewAttachment = (SwipeListView) findViewById(R.id.listView_attachment);
        tv_title.setText("附件");

        img_title_left.setOnTouchListener(Global.GetTouch());
        tv_upload.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        if (!isAdd) {
            // tv_upload.setVisibility(View.GONE);
        }

        if (isPic && !isCreat) {
            getPageData();
        } else if (isCreat) {//上传附件就取消loading
            ll_loading.setStatus(LoadingLayout.Success);
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
        showLoading("");
        buildAttachment();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class)
                .setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);
                        getAttachments();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    /**
     * 获取附件列表信息
     */
    void getAttachments() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse("获取附件", response);
                ll_loading.setStatus(LoadingLayout.Success);
                mListAttachment = attachments;
                attachmentCount = attachments.size();
                bindAttachment();
                controller.removeAllTask();
                if (attachmentCount == 0)
                    ll_loading.setStatus(LoadingLayout.Empty);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error, ll_loading);
                controller.removeAllTask();
//                finish();
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

        //Attachment.Sort(mListAttachment);
        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mContext, mListAttachment, mUserList, bizType, uuid, isOver);
            adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
                @Override
                public void afterDelete(final Attachment attachment) {
                    //附件删除 重新绑定
                    mListAttachment.remove(attachment);
                    attachmentCount = mListAttachment.size();
                    adapter.notifyDataSetChanged();
                }
            });
            mListViewAttachment.setSwipeCloseAllItemsWhenMoveList(true);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
        }
        adapter.refreshData();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //后退
            case R.id.img_title_left:
                onBackPressed();
                break;

            //上传
            case R.id.tv_upload:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        mIntent = new Intent();
        mIntent.putExtra("uuid", uuid);
        mIntent.putExtra("size", attachmentCount);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    if (mSelectPath.size() > 0) {
                        showLoading("");
                        controller.startUpload();
                    }
                }
                break;
        }
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
        cancelLoading();

        // TODO: 上传失败提醒
        if (taskList.size() > 0) {
            postAttaData();
        }
    }
}
