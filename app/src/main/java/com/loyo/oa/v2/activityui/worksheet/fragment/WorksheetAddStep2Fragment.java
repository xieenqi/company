package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.PreviewImageAddActivity;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetWrapper;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.loyo.oa.v2.application.MainApp.PICTURE;

public class WorksheetAddStep2Fragment extends BaseFragment implements View.OnClickListener, UploadControllerCallback {
    private View mView;
    private ViewGroup img_title_left, img_title_right;
    TextView tv_title_1;
    EditText edt_title, edt;

    private ImageUploadGridView gridView;
    UploadController controller;

    private String uuid = StringUtil.getUUID();
    private int bizType = 29;
    private ArrayList<AttachmentBatch> attachment;

    WorksheetAddActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (WorksheetAddActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_worksheet_add_step2, null);
            controller = new UploadController(this.getActivity(), 9);
            controller.setObserver(this);
            initUI(mView);
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
            edt_title.setText(mActivity.selectedOrder.title +"-"+ mActivity.selectedType.name);
        }
        controller.loadView(gridView);
    }

    public void addPhoto(List<String> photos) {
        for (String path : photos) {
            controller.addUploadTask("file://" + path, null,  uuid);
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
                ((WorksheetAddActivity)getActivity()).previousStep();
                break;
            case R.id.img_title_right:

                showLoading("");
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


        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("title", title);
        map.put("orderId", orderId);
        map.put("orderName", orderName);
        map.put("templateId", templateId);
        if (content != null) {
            map.put("content", content);
        }
        if (attachment!= null && attachment.size() > 0) {
            map.put("uuid", uuid);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).addWorksheet(map, new Callback<WorksheetWrapper>() {
            @Override
            public void success(WorksheetWrapper wrapper, Response response) {
                if (wrapper.errcode == 0) {
                    Intent intent = new Intent();
                    intent.putExtra(ExtraAndResult.EXTRA_BOOLEAN, true);

                    Worksheet ws = wrapper.data;
                    if (ws == null) {
                        ws = new Worksheet();
                    }
                    AppBus.getInstance().post(ws);

                    app.finishActivity(getActivity(), MainApp.ENTER_TYPE_LEFT, 0, intent);
                }
                else {
                    Toast("" + wrapper.errmsg);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
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

        buildAttachment();

        IAttachment service = RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class);

        service.setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);

                        // TODO:
                        commitWorksheet();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
        showLoading("");
    }

    @Override
    public void onAddEvent(UploadController controller) {
        Intent intent = new Intent(this.getActivity(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9-controller.count()) /*最大可选择图片数量*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
        this.startActivityForResult(intent, PICTURE);
    }

    @Override
    public void onItemSeclected(UploadController controller, int index) {

        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<SelectPicPopupWindow.ImageInfo> newAttachment = new ArrayList<>();
        int newPosistion = index;

        for (int i = 0; i < taskList.size(); i++) {
            SelectPicPopupWindow.ImageInfo attachment = new SelectPicPopupWindow.ImageInfo(taskList.get(i).getValidatePath());
            newAttachment.add(attachment);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", newAttachment);
        bundle.putInt("position", newPosistion);
        bundle.putBoolean("isEdit", true);
        MainApp.getMainApp().startActivityForResult((Activity) this.getActivity(), PreviewImageAddActivity.class,
                MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
        cancelLoading();
        if (taskList.size() >0) {
            postAttaData();
        }
        else {
            commitWorksheet();
        }
    }

}
