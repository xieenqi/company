package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.other.PreviewImageAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.event.OrderWorksheetAddFinish;
import com.loyo.oa.v2.activityui.worksheet.presenter.OrderWorksheetAddPresenter;
import com.loyo.oa.v2.activityui.worksheet.presenter.impl.OrderWorksheetAddPresenterImpl;
import com.loyo.oa.v2.activityui.worksheet.viewcontrol.OrderWorksheetAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 【订单添加工单】
 * Created by yyy on 16/10/22.
 */

public class OrderWorksheetAddActivity extends BaseActivity implements View.OnClickListener, OrderWorksheetAddView, UploadControllerCallback {

    private EditText edt_content;
    private ImageUploadGridView gridView;
    private LinearLayout ll_intent_kind;

    private TextView tv_kind;
    private EditText et_title;

    private ViewGroup img_title_left;
    private ViewGroup img_title_right;

    private OrderWorksheetAddPresenter mPresenter;
    private OrderWorksheetListModel mOworssheetList = new OrderWorksheetListModel();

    private UploadController controller;

    private String uuid = StringUtil.getUUID();
    private int bizType = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderworksheetadd);
        initUI();
    }

    private void initUI() {
        controller = new UploadController(OrderWorksheetAddActivity.this, 9);
        controller.setObserver(this);

        edt_content = (EditText) findViewById(R.id.edt_content);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        et_title = (EditText) findViewById(R.id.et_title);
        ll_intent_kind = (LinearLayout) findViewById(R.id.ll_intent_kind);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);

        ll_intent_kind.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        mPresenter = new OrderWorksheetAddPresenterImpl(mContext, this);

        controller.loadView(gridView);
    }

    /**
     * 回调跳转
     */
    public void setResultIntent(int size) {
        LogUtil.dee("size:" + size);
        mOworssheetList.size = size;
        mOworssheetList.uuid = uuid;
        mOworssheetList.content = edt_content.getText().toString();
        OrderWorksheetAddFinish event = new OrderWorksheetAddFinish();
        event.bundle = new Bundle();
        event.bundle.putSerializable(ExtraAndResult.EXTRA_ID, mOworssheetList);
        AppBus.getInstance().post(event);
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*回退*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*提交*/
            case R.id.img_title_right:
                showLoading("");
                controller.startUpload();
                controller.notifyCompletionIfNeeded();
                break;

            /*工单类型*/
            case R.id.ll_intent_kind:
                mPresenter.getWorkSheetType(sweetAlertDialogView);
                break;

            default:
                break;

        }
    }

    /**
     * 选择工单类型处理
     */
    @Override
    public void getWorkSheetTypeEmbl(int index, String value, SweetAlertDialogView sweetAlertDialogView, ArrayList<WorksheetTemplate> types) {

        WorksheetTemplate template = types.get(index - 1);
        if (template.hasItems == false) {
            sweetAlertDialogView.alertIcon(null, "该工单类型未配置模版,请选择其他类型!");
            return;
        }
        tv_kind.setText(value);
        if (TextUtils.isEmpty(et_title.getText().toString())) {
            et_title.setText(OrderAddActivity.orderTitle + "-" + value);
        }

        mOworssheetList.templateName = template.name;
        mOworssheetList.templateId = template.id;
        mOworssheetList.title = OrderAddActivity.orderTitle + "" + value;
    }

    /**
     * 上传附件成功处理
     * */
    @Override
    public void setUploadAttachmentEmbl(int size) {
        setResultIntent(size);
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    /**
     * 添加图片跳转处理
     */
    @Override
    public void onAddEvent(UploadController controller) {
        Intent intent = new Intent(OrderWorksheetAddActivity.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9 - controller.count()) /*最大可选择图片数量*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
        startActivityForResult(intent, MainApp.PICTURE);
    }

    /**
     * 预览图片
     */
    @Override
    public void onItemSelected(UploadController controller, int index) {
        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<SelectPicPopupWindow.ImageInfo> newAttachment = new ArrayList<>();
        int newPosistion = index;

        for (int i = 0; i < taskList.size(); i++) {
            SelectPicPopupWindow.ImageInfo attachment = new SelectPicPopupWindow.ImageInfo("file://" + taskList.get(i).getValidatePath());
            newAttachment.add(attachment);
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", newAttachment);
        bundle.putInt("position", newPosistion);
        bundle.putBoolean("isEdit", true);
        MainApp.getMainApp().startActivityForResult(OrderWorksheetAddActivity.this, PreviewImageAddActivity.class,
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
        if (taskList.size() > 0) {
            mPresenter.uploadAttachmentAt(controller,uuid,bizType);
        } else {
            setResultIntent(taskList.size());
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
                    List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    mPresenter.addPhoto(mSelectPath, controller, uuid);
                }
                break;

            /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                mPresenter.removeAttachmentAt(data.getExtras().getInt("position"), controller);
                break;

            default:
                break;

        }
    }

    @Override
    public void showProgress(String message) {
        showLoading(message);
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }
}
