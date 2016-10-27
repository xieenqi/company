package com.loyo.oa.v2.activityui.worksheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MapSingleView;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.model.HttpLoc;
import com.loyo.oa.v2.activityui.other.PreviewImageAddActivity;

import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;

import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IWorksheet;

import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
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

/**
 * 【提交完成/打回重做】工单
 * Created by yyy on 16/8/30.
 */
public class WorksheetSubmitActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback {

    /**
     * UI
     */
    private TextView tv_title_1;
    private ImageUploadGridView gridView;
    private TextView tv_address;        //定位内容
    private EditText view_edit;         //输入content
    private RelativeLayout img_title_right;
    private LinearLayout img_title_left,  //返回
            layout_image,    //图片
            layout_location, //定位
            layout_address,  //定位layout
            layout_delete_location,  //定位删除
            layout_address_info;     //定位信息

    /**
     * Data
     */
    private int fromPage;                //判断来自的页面(打回重做0x10 提交完成0x02)
    private int type;                    //type 1为提交完成，2为打回重做
    private int bizType = 29;            //附件type
    private String uuid = StringUtil.getUUID();
    private String id;                  //事件Id
    private double laPosition;          //当前位置的经纬度
    private double loPosition;
    private HttpLoc httpLoc = new HttpLoc();
    private List<String> mSelectPath;
    private PositionResultItem positionResultItem;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    UploadController controller;


    /**
     * Other
     */
    private Bundle mBundle;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_submit);
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        initUI();
    }

    void initUI() {
        mIntent = getIntent();
        fromPage = mIntent.getIntExtra(ExtraAndResult.EXTRA_DATA, 0x01);
        id = mIntent.getStringExtra(ExtraAndResult.CC_USER_ID);
        if (TextUtils.isEmpty(id)) {
            Toast("参数不全");
            onBackPressed();
        }
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_address = (TextView) findViewById(R.id.tv_address);

        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_location = (LinearLayout) findViewById(R.id.layout_location);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        layout_address_info = (LinearLayout) findViewById(R.id.layout_address_info);
        layout_delete_location = (LinearLayout) findViewById(R.id.layout_delete_location);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
        view_edit = (EditText) findViewById(R.id.view_edit);

        if (fromPage == 0x01) {
            super.setTitle("打回重做");
            view_edit.setHint("请输入重做原因，必填");
            type = 2;
        } else {
            super.setTitle("提交完成");
            view_edit.setHint("请输入完成内容，非必填");
            type = 1;
        }

        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        layout_image.setOnClickListener(this);
        layout_location.setOnClickListener(this);
        layout_delete_location.setOnClickListener(this);
        layout_address_info.setOnClickListener(this);

        img_title_left.setOnTouchListener(Global.GetTouch());
        layout_image.setOnTouchListener(Global.GetTouch());
        layout_location.setOnTouchListener(Global.GetTouch());
        layout_delete_location.setOnTouchListener(Global.GetTouch());
        controller.loadView(gridView);
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
    void postAttaData() {
        showLoading("");
        buildAttachment();
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class)
                .setAttachementData(attachment, new Callback<ArrayList<AttachmentForNew>>() {
                    @Override
                    public void success(ArrayList<AttachmentForNew> attachmentForNew, Response response) {
                        HttpErrorCheck.checkResponse("上传附件信息", response);
                        commitDynamic();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 提交事件处理数据
     */
    void commitDynamic() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("content", view_edit.getText().toString());
        map.put("uuid", uuid);
        map.put("address", httpLoc);
        LogUtil.dee("提交事件信息：" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).setEventSubmit(id, map, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                HttpErrorCheck.checkResponse("提交事情处理信息", response);
                if (type == 1) {
                    AppBus.getInstance().post(new WorksheetDetail());
                    WorksheetEventChangeEvent event = new WorksheetEventChangeEvent();
                    event.bundle = new Bundle();
                    event.bundle.putString(ExtraAndResult.EXTRA_ID, id);
                    AppBus.getInstance().post(event);
                } else {
                    AppBus.getInstance().post(new WorksheetInfo());
                }
                app.finishActivity(WorksheetSubmitActivity.this, MainApp.ENTER_TYPE_LEFT, 0, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*提交*/
            case R.id.img_title_right:
                if (fromPage == 0x01) {
                    if (TextUtils.isEmpty(view_edit.getText().toString())) {
                        Toast("请输入重做原因！");
                        return;
                    }
                }

                showLoading("");
                controller.startUpload();
                controller.notifyCompletionIfNeeded();

                break;

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*图片选择*/
            case R.id.layout_image:
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9-controller.count()) /*最大可选择图片数量*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
                this.startActivityForResult(intent, PICTURE);
                break;

            /*定位选择*/
            case R.id.layout_location:
                mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                break;

            /*删除地址*/
            case R.id.layout_delete_location:
                httpLoc = new HttpLoc();
                layout_address.setVisibility(View.GONE);
                break;

            /*位置信息*/
            case R.id.layout_address_info:
                mIntent = new Intent(WorksheetSubmitActivity.this, MapSingleView.class);
                mIntent.putExtra("la", laPosition);
                mIntent.putExtra("lo", loPosition);
                mIntent.putExtra("address", httpLoc.addr);
                startActivity(mIntent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null,  uuid);
                    }
                    controller.reloadGridView();

                }
                break;

           /*附件删除 回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                controller.removeTaskAt(data.getExtras().getInt("position"));
                controller.reloadGridView();
                break;

            /*地址定位 回调*/
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    httpLoc.loc.add(positionResultItem.loPosition);
                    httpLoc.loc.add(positionResultItem.laPosition);
                    httpLoc.addr = positionResultItem.address;
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    layout_address.setVisibility(View.VISIBLE);
                    tv_address.setText(positionResultItem.address);
                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9-controller.count()) /*最大可选择图片数量*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
        this.startActivityForResult(intent, PICTURE);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {

        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<SelectPicPopupWindow.ImageInfo> newAttachment = new ArrayList<>();
        int newPosistion = index;

        for (int i = 0; i < taskList.size(); i++) {
            SelectPicPopupWindow.ImageInfo attachment = new SelectPicPopupWindow.ImageInfo("file://"+taskList.get(i).getValidatePath());
            newAttachment.add(attachment);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", newAttachment);
        bundle.putInt("position", newPosistion);
        bundle.putBoolean("isEdit", true);
        MainApp.getMainApp().startActivityForResult((Activity) mContext, PreviewImageAddActivity.class,
                MainApp.ENTER_TYPE_BUTTOM, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
        cancelLoading();
        int count = controller.failedTaskCount();
        if (count > 0) {
            Toast(count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() >0) {
            postAttaData();
        }
        else {
            commitDynamic();
        }
    }
}
