package com.loyo.oa.v2.activityui.clue;

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
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新建跟进动态】销售线索
 */
public class ClueDynamicAddActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback {

    private ImageUploadGridView gridView;
    private ViewGroup img_title_left, img_title_right, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_contact_name;
    private LinearLayout ll_contact, ll_contactItem;
    private LinearLayout layout_image;

    private String clueId;
    private String tagItemIds, contactId, contactName = "无";
    private String content;
    private String uuid = StringUtil.getUUID();
    private int bizType = 17;

    private List<String> mSelectPath;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    UploadController controller;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_followup_create);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            clueId = bundle.getString(ExtraAndResult.EXTRA_ID);
            contactName = bundle.getString(ExtraAndResult.EXTRA_NAME);
        }

        controller = new UploadController(this, 9);
        controller.setObserver(this);
        initUI();
        getTempSaleActivity();

    }

    void getTempSaleActivity() {
        if (mSaleActivity == null) {
            return;
        }

        edt.setText(mSaleActivity.getContent());
    }

    void initUI() {
        super.setTitle("写跟进");

        edt = (EditText) findViewById(R.id.edt);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        layout_image.setOnClickListener(this);
        layout_image.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);


        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_contact.setOnTouchListener(touch);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_contactItem.setVisibility(null == clueId ? View.GONE : View.VISIBLE);
        tv_contact_name.setText(contactName);

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
    public void postAttaData() {
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
     * 提交数据
     */
    public void commitDynamic() {


        if (StringUtil.isEmpty(content)) {
            Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
            return;
        } else if (TextUtils.isEmpty(tagItemIds)) {
            Toast("请选择跟进方式");
            return;
        } else if (TextUtils.isEmpty(clueId)) {
            Toast("请选择跟进线索");
            return;
        }

        cancelLoading();
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("sealsleadId", clueId);
        map.put("content", content);
        map.put("typeId", tagItemIds);
        if (contactName != null) {
            map.put("contactName", contactName);
        }
        if (attachment.size() != 0) {
            map.put("uuid", uuid);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).addSaleactivity(map, new RCallback<SaleActivity>() {
            @Override
            public void success(final SaleActivity saleActivity, final Response response) {
                HttpErrorCheck.checkResponse("新建跟进动态", response);
                cancelLoading();
                app.finishActivity(ClueDynamicAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                cancelLoading();
            }
        });

    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;

            /*提醒时间*/
            case R.id.layout_remain_time:
                //selectRemainTime();
                break;

            /*跟进方式*/
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                loseBundle.putString("tagName", tv_sale_action.getText().toString());
                app.startActivityForResult(this, CommonTagSelectActivity_.class, app.ENTER_TYPE_RIGHT, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            /*提交数据*/
            case R.id.img_title_right:
                content = edt.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                }
                showLoading("");
                controller.startUpload();
                controller.notifyCompletionIfNeeded();
                break;

            /*选择图片*/
            case R.id.layout_image:
                PhotoPicker.builder()
                        .setPhotoCount(9 - controller.count())
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this);

                break;
        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clueId) {
            DBManager.Instance().deleteSaleActivity("clue" + clueId);
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), "clue" + clueId);
            }
        }
    }

    /**
     * 获取跟进方式
     *
     * @param tags
     * @return
     */
    private String getSaleTypes(final ArrayList<CommonTag> tags) {
        if (null == tags || tags.isEmpty()) {
            return "";
        }
        StringBuilder reasons = new StringBuilder();
        int index = 0;
        for (CommonTag reson : tags) {
            reasons.append(reson.getName());
            if (index < tags.size() - 1) {
                reasons.append(",");
            }
            index++;
        }
        return reasons.toString();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            /*跟进方式 回调*/
            case CommonTagSelectActivity.REQUEST_TAGS:
                ArrayList<CommonTag> tags = (ArrayList<CommonTag>) data.getSerializableExtra("data");
                tv_sale_action.setText(getSaleTypes(tags));
                tagItemIds = tags.get(0).getId();
                break;

            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    controller.reloadGridView();
                }
                break;
            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null){
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        controller.removeTaskAt(index);
                        controller.reloadGridView();
                    }
                }
                break;

        }
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
                .start(this);
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
            postAttaData();
        } else {
            commitDynamic();
        }
    }
}

