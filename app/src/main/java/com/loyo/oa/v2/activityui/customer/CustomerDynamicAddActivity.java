package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
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
import com.loyo.oa.v2.activityui.commonview.CommonRecordItem;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.other.PreviewImageAddActivity;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.loyo.oa.v2.application.MainApp.PICTURE;

/**
 * 【新建跟进】客户管理
 * <p/>
 * Create by yyy on 16/08/24
 */
public class CustomerDynamicAddActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback {

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private ImageUploadGridView gridView;
    UploadController controller;
    private LinearLayout layout_image, ll_root, ll_record;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time, tv_customer, tv_contact_name;
    private Customer mCustomer;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_customer, ll_contact, ll_contactItem;

    private String content;
    private String uuid = StringUtil.getUUID();
    private int bizType = 17;

    private List<String> mSelectPath;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_activities_add);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
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
//        layout_image = (LinearLayout) findViewById(R.id.layout_image);
        tv_remain_time = (TextView) findViewById(R.id.tv_remain_time);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_remain_time = (ViewGroup) findViewById(R.id.layout_remain_time);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        ll_record = (LinearLayout) findViewById(R.id.ll_record);
//        layout_image.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        layout_sale_action.setOnClickListener(this);
        layout_remain_time.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        ll_customer.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_customer.setVisibility(null == mCustomer ? View.VISIBLE : View.GONE);
        ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
        Global.SetTouchView(img_title_left, layout_sale_action, layout_remain_time, img_title_right, ll_customer, ll_contact);
        if (null != mCustomer) {
            getDefaultContact(mCustomer.contacts);
        }
        controller.loadView(gridView);
        initMultiFunctionModule();


    }

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        final MultiFunctionModule mfmodule = new MultiFunctionModule(this);
        ll_root.addView(mfmodule);
        mfmodule.setRecordClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((boolean) v.getTag()) {
                    showInputKeyboard(edt);
                    mfmodule.setIsRecording(false);
                    v.setTag(false);
                } else {
                    hideInputKeyboard(edt);
                    mfmodule.setIsRecording(true);
                    v.setTag(true);
                }

            }
        });
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfmodule.setIsRecording(false);
            }
        });
        mfmodule.setRecordComplete(new MultiFunctionModule.RecordComplete() {
            @Override
            public void recordComplete(String recordPath, String tiem) {
                ll_record.addView(new CommonRecordItem(CustomerDynamicAddActivity.this, recordPath, tiem));
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
     * 提交新建跟进
     */
    public void commitDynamic() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", mCustomer.getId());
        map.put("content", content);
        map.put("typeId", tagItemIds);
        if (attachment.size() != 0) {
            map.put("uuid", uuid);
        }
        if (!tv_remain_time.getText().toString().isEmpty() || !tv_remain_time.getText().toString().equals("不提醒")) {
            map.put("remindAt", DateTool.getDateToTimestamp(tv_remain_time.getText().toString().trim(), app.df2) / 1000);
        }
        if (!TextUtils.isEmpty(contactId)) {
            map.put("contactId", contactId);
            map.put("contactName", contactName);
        }
        LogUtil.dee("新建跟进:" + MainApp.gson.toJson(map));

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSaleactivity(map, new RCallback<SaleActivity>() {
            @Override
            public void success(final SaleActivity saleActivity, final Response response) {
                HttpErrorCheck.checkResponse("新建跟进动态", response);
                app.finishActivity(CustomerDynamicAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
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
     * 获取客户的默认联系人
     *
     * @param data
     */
    private void getDefaultContact(ArrayList<Contact> data) {
        for (Contact ele : data) {
            if (!ele.isDefault()) {
                continue;
            } else {
                contactId = ele.getId();
                contactName = ele.getName();
                tv_contact_name.setText(contactName);
            }
        }
    }

    /**
     * 选择下次跟进时间
     */
    private void selectRemainTime() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                tv_remain_time.setText(str);
            }

            @Override
            public void onCancel() {
                tv_remain_time.setText("不提醒");
            }
        }, false, "不提醒");
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;

            /*下次跟进提醒*/
            case R.id.layout_remain_time:
                selectRemainTime();
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

            /*提交*/
            case R.id.img_title_right:

                content = edt.getText().toString().trim();
                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                } else if (null == mCustomer || TextUtils.isEmpty(mCustomer.getId())) {
                    Toast("请选择跟进客户");
                    return;
                }

                showLoading("");
                controller.startUpload();
                controller.notifyCompletionIfNeeded();

                break;

            /*选择客户*/
            case R.id.ll_customer:
                Bundle b = new Bundle();
                app.startActivityForResult(CustomerDynamicAddActivity.this, SigninSelectCustomer.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                break;

            /*选择联系人*/
            case R.id.ll_contact:
                Bundle bContact = new Bundle();
                bContact.putSerializable(ExtraAndResult.EXTRA_DATA, mCustomer.contacts);
                bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                app.startActivityForResult(CustomerDynamicAddActivity.this, FollowContactSelectActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                break;

            /*选择图片*/
            case R.id.layout_image:
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true /*是否显示拍摄图片*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9 - controller.count()) /*最大可选择图片数量*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI  /*选择模式*/);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_CROP_CIRCLE, false);
                this.startActivityForResult(intent, PICTURE);
                break;

        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomer) {
            DBManager.Instance().deleteSaleActivity(mCustomer.getId());
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), mCustomer.getId());
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

            /*选择客户返回 回调*/
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                String customerName = "无";
                if (null != customer) {
                    mCustomer = customer;
                    customerName = customer.name;
                    getDefaultContact(mCustomer.contacts);
                }
                tv_customer.setText(customerName);
                ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
                break;

           /* 选择客户联系人 回调*/
            case ExtraAndResult.REQUEST_CODE_STAGE:
                Contact contact = (Contact) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                if (null != contact) {
                    contactId = contact.getId();
                    contactName = contact.getName();
                }
                tv_contact_name.setText(contactName);
                break;

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    controller.reloadGridView();

                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                controller.removeTaskAt(data.getExtras().getInt("position"));
                controller.reloadGridView();
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
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, (9 - controller.count()) /*最大可选择图片数量*/);
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
            SelectPicPopupWindow.ImageInfo attachment = new SelectPicPopupWindow.ImageInfo("file://" + taskList.get(i).getValidatePath());
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
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            commitDynamic();
        }
    }
}
