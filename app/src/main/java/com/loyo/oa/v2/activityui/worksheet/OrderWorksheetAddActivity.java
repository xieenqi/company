package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.event.OrderWorksheetAddFinish;
import com.loyo.oa.v2.activityui.worksheet.presenter.OrderWorksheetAddPresenter;
import com.loyo.oa.v2.activityui.worksheet.presenter.impl.OrderWorksheetAddPresenterImpl;
import com.loyo.oa.v2.activityui.worksheet.viewcontrol.OrderWorksheetAddView;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 【订单添加工单】
 * Created by yyy on 16/10/22.
 */

public class OrderWorksheetAddActivity extends BaseActivity implements View.OnClickListener, OrderWorksheetAddView, UploadControllerCallback {

    private ImageUploadGridView gridView;
    private LinearLayout ll_intent_kind;
    private TextView tv_kind;
    private TextView tv_title;
    private EditText et_title;
    private EditText edt_content;
    private ViewGroup img_title_left;
    private ViewGroup img_title_right;

    private OrderWorksheetAddPresenter mPresenter;
    private OrderWorksheetListModel mOrderWorksheet;
    private UploadController controller;

    private String uuid = StringUtil.getUUID();
    private int bizType = 29;
    private int position;
    private boolean isEdit = false;  //是否为编辑

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderworksheetadd);
        initUI();
    }

    /**
     * 初始化
     */
    private void initUI() {

        controller = new UploadController(OrderWorksheetAddActivity.this, 9);
        controller.setObserver(this);

        edt_content = (EditText) findViewById(R.id.edt_content);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        tv_title = (TextView) findViewById(R.id.tv_title_1);
        et_title = (EditText) findViewById(R.id.et_title);
        ll_intent_kind = (LinearLayout) findViewById(R.id.ll_intent_kind);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);

        ll_intent_kind.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        mPresenter = new OrderWorksheetAddPresenterImpl(mContext, this);
        tv_title.setText("新建工单");
        controller.loadView(gridView);

        if (null != getIntent().getSerializableExtra(ExtraAndResult.EXTRA_NAME)) {
            mOrderWorksheet = (OrderWorksheetListModel) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_NAME);
        } else {
            mOrderWorksheet = new OrderWorksheetListModel();
        }
        editUI();
    }

    /**
     * 设置编辑参数
     */
    private void editUI() {

        if (null == mOrderWorksheet.templateName || TextUtils.isEmpty(mOrderWorksheet.templateName)) {
            return;
        }

        position = getIntent().getIntExtra(ExtraAndResult.APP_START, 0);
        LogUtil.dee("获取到的position:" + position);

        isEdit = true;
        tv_kind.setText(mOrderWorksheet.templateName);
        edt_content.setText(mOrderWorksheet.content);
        et_title.setText(mOrderWorksheet.title);

/*        if (TextUtils.isEmpty(et_title.getText().toString())) {
            if (TextUtils.isEmpty(OrderAddActivity.orderTitle)) {
                et_title.setText(mOrderWorksheet.templateName);
            } else {
                et_title.setText(OrderAddActivity.orderTitle + "-" + mOrderWorksheet.templateName);
            }
        }*/

        if (null != mOrderWorksheet.mSelectPath) {
            mPresenter.addPhoto(mOrderWorksheet.mSelectPath, controller, uuid);
        }
    }

    /**
     * 回调跳转
     */
    public void setResultIntent(int size) {
        mOrderWorksheet.size = size;
        mOrderWorksheet.uuid = uuid;
        mOrderWorksheet.content = edt_content.getText().toString();
        OrderWorksheetAddFinish event = new OrderWorksheetAddFinish();
        event.bundle = new Bundle();
        event.bundle.putSerializable(ExtraAndResult.EXTRA_ID, mOrderWorksheet);
        event.bundle.putBoolean(ExtraAndResult.WELCOM_KEY, isEdit);
        event.bundle.putInt(ExtraAndResult.APP_START, position);
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

                if (TextUtils.isEmpty(tv_kind.getText().toString())) {
                    Toast("请选择工单类型!");
                    return;
                }

                mOrderWorksheet.title = et_title.getText().toString();
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
            if (TextUtils.isEmpty(OrderAddActivity.orderTitle)) {
                et_title.setText(value);
            } else {
                et_title.setText(OrderAddActivity.orderTitle + "-" + value);
            }
        }

        mOrderWorksheet.templateName = template.name;
        mOrderWorksheet.templateId = template.id;
    }

    /**
     * 上传附件成功处理
     */
    @Override
    public void setUploadAttachmentEmbl(int size, List<String> mFilePath) {
        setResultIntent(size);
        mOrderWorksheet.mSelectPath = mFilePath;
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
        PhotoPicker.builder()
                .setPhotoCount(9 - controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this);
    }

    /**
     * 预览图片
     */
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
                .start(this);
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
            mPresenter.uploadAttachmentAt(controller, uuid, bizType);
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
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    mPresenter.addPhoto(mSelectPath, controller, uuid);
                }
                break;
            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null){
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        mPresenter.removeAttachmentAt(index, controller);
                    }
                }
                break;

            default:
                break;

        }
    }

    @Override
    public void showStatusProgress() {

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
