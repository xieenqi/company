package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WorksheetAddStep2Fragment extends BaseFragment implements View.OnClickListener, UploadControllerCallback {
    private View mView;
    private ViewGroup img_title_left, img_title_right;
    TextView tv_title_1;
    EditText edt_title, edt;

    private ImageUploadGridView gridView;
    UploadController controller;

    private String uuid;
    private int bizType = 29;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();

    WorksheetAddActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (WorksheetAddActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_worksheet_add_step2, null);
            controller = new UploadController(this.getActivity(), 9);
            controller.setObserver(this);

            initUI(mView);

            controller.loadView(gridView);
        }
        return mView;
    }

    void initUI(View mView) {

        gridView = (ImageUploadGridView) mView.findViewById(R.id.image_upload_grid_view);

        edt_title = (EditText) mView.findViewById(R.id.edt_title);
        edt = (EditText) mView.findViewById(R.id.edt);

        img_title_left = (ViewGroup) mView.findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);

        img_title_right = (ViewGroup) mView.findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);

        tv_title_1 = (TextView) mView.findViewById(R.id.tv_title_1);
        tv_title_1.setText("填写工单内容");

        if (mActivity != null) {
            edt_title.setText(mActivity.selectedOrder.title + "-" + mActivity.selectedType.name);
        }
        controller.loadView(gridView);
    }

    public void addPhoto(List<String> photos) {
        for (String path : photos) {
            controller.addUploadTask("file://" + path, null, uuid);
        }
        controller.reloadGridView();
    }

    public void removeAttachmentAt(int index) {
        controller.removeTaskAt(index);
        controller.reloadGridView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                ((WorksheetAddActivity) getActivity()).previousStep();
                break;
            case R.id.img_title_right:
                uuid = StringUtil.getUUID();
                showStatusLoading(false);
                controller.startUpload();
                controller.notifyCompletionIfNeeded();
                break;
        }
    }

    private void commitWorksheet() {

        /**
         * 新建工单
         *
         * @param body     {
         *                 "title"      :"工单001",
         *                 "orderId"    :"57c3ef26ebe07f2d0b000001",
         *                 "orderName"  :"新建的工单",
         *                 "templateId" :"57c3ef26ebe07f2d0b000001",
         *                 "content"    : "工单事件很多哟!",
         *                 "uuid"       :"57c3ef26ebe07f2d0b000001"
         *                 }
         * @param callback
         */

        String title = edt_title.getText().toString().trim();
        if (title == null && title.length() <= 0) {
            Toast("标题不能为空");
            return;
        }
        if (title.length() > 50) {
            Toast("标题长度不能超过50个字符");
            return;
        }

        String content = edt.getText().toString().trim();
        String orderId = mActivity.selectedOrder.id;
        String orderName = mActivity.selectedOrder.title;
        String templateId = mActivity.selectedType.id;

        showStatusLoading(false);
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", title);
        map.put("orderId", orderId);
        map.put("orderName", orderName);
        map.put("templateId", templateId);
        if (content != null) {
            map.put("content", content);
        }
        if (attachment != null && attachment.size() > 0) {
            map.put("uuid", uuid);
        }

        WorksheetService.addWorksheet(map)
                .subscribe(new DefaultLoyoSubscriber<Worksheet>(LoyoErrorChecker.COMMIT_DIALOG) {
                    @Override
                    public void onNext(final Worksheet worksheet) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cancelStatusLoading();
                                Intent intent = new Intent();
                                intent.putExtra(ExtraAndResult.EXTRA_BOOLEAN, true);
                                Worksheet ws = worksheet;
                                if (ws == null) {
                                    ws = new Worksheet();
                                }
                                AppBus.getInstance().post(ws);
                                app.finishActivity(getActivity(), MainApp.ENTER_TYPE_LEFT, 0, intent);
                            }
                        },1000);
                    }
                });
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
        showStatusLoading(false);
        buildAttachment();
//        IAttachment service = RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT())
//                .create(IAttachment.class);
//        service.setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
//            @Override
//            public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
//                HttpErrorCheck.checkCommitSus("上传附件信息",response);
//                cancelStatusLoading();
//                commitWorksheet();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkCommitEro(error);
//            }
//        });

        AttachmentService.setAttachementData(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<AttachmentForNew>>(LoyoErrorChecker.COMMIT_DIALOG) {
                    @Override
                    public void onNext(ArrayList<AttachmentForNew> news) {
                        cancelStatusLoading();
                        commitWorksheet();
                    }
                });
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoPicker.builder()
                .setPhotoCount(9 - controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this.getActivity());
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {

        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://"));
            {
                path = path.replace("file://", "");
            }
            selectedPhotos.add(path);
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(true)
                .start(this.getActivity());
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
        cancelStatusLoading();
        int count = controller.failedTaskCount();
        if (count > 0) {
            Toast(count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            commitWorksheet();
        }
    }

}
