package com.loyo.oa.v2.activityui.followup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueSearchActivity;
import com.loyo.oa.v2.activityui.clue.ClueTypeEnum;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.commonview.CommonRecordItem;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.RecordUtils;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.customer.FollowContactSelectActivity;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.followup.event.FollowUpRushEvent;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomerSearch;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.CommonIdName;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

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
public class DynamicAddActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback {

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private ImageUploadGridView gridView;
    UploadController controller;
    private LinearLayout ll_root, ll_record, ll_location, ll_at, ll_clue_company, ll_clue;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time, tv_customer, tv_contact_name, tv_location_text,
            tv_at_text, tv_clue_company, tv_clue_name;
    private Customer mCustomer;
    private ClueListItem mClue;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_customer, ll_contact;
    private ImageView iv_location_delete, iv_at_delete;
    private String content;
    private String uuid = StringUtil.getUUID();
    private int bizType = 17;
    private List<String> mSelectPath;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    private ArrayList<Record> audioInfo = new ArrayList<>();//录音数据
    private Location location;//添加的定位信息数据
    private List<CommonIdName> atDepts = new ArrayList<>();//@的部门
    private List<String> atUserIds = new ArrayList<>();//@的人员
    private StaffMemberCollection collection;//选人返回的数据
    private boolean isCustom;//是否是客户写跟进 否则就是是线索写跟进

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_activities_add);
        getIntentData();
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        initUI();
        getTempSaleActivity();
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
            mClue = (ClueListItem) bundle.getSerializable(ClueListItem.class.getName());
            int action = bundle.getInt(ExtraAndResult.DYNAMIC_ADD_ACTION, 0);
            if (action == ExtraAndResult.DYNAMIC_ADD_CULE) {
                isCustom = false;
                contactName = mClue.responsorName;
            } else {
                isCustom = true;
            }
        }
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
        ll_location = (LinearLayout) findViewById(R.id.ll_location);
        tv_remain_time = (TextView) findViewById(R.id.tv_remain_time);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
//        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_remain_time = (ViewGroup) findViewById(R.id.layout_remain_time);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        //客户
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        //线索
        ll_clue_company = (LinearLayout) findViewById(R.id.ll_clue_company);
        ll_clue = (LinearLayout) findViewById(R.id.ll_clue);
        tv_clue_company = (TextView) findViewById(R.id.tv_clue_company);
        tv_clue_name = (TextView) findViewById(R.id.tv_clue_name);
        ll_root = (LinearLayout) findViewById(R.id.ll_root);
        ll_record = (LinearLayout) findViewById(R.id.ll_record);
        tv_location_text = (TextView) findViewById(R.id.tv_location_text);
        iv_location_delete = (ImageView) findViewById(R.id.iv_location_delete);
        iv_at_delete = (ImageView) findViewById(R.id.iv_at_delete);
        ll_at = (LinearLayout) findViewById(R.id.ll_at);
        tv_at_text = (TextView) findViewById(R.id.tv_at_text);
        img_title_left.setOnClickListener(this);
        layout_sale_action.setOnClickListener(this);
        layout_remain_time.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        ll_customer.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        iv_location_delete.setOnClickListener(this);
        iv_at_delete.setOnClickListener(this);
        ll_clue_company.setOnClickListener(this);
        ll_customer.setVisibility(isCustom ? View.VISIBLE : View.GONE);
        ll_contact.setVisibility(isCustom ? View.VISIBLE : View.GONE);
        ll_clue_company.setVisibility(isCustom ? View.GONE : View.VISIBLE);
        ll_clue.setVisibility(isCustom ? View.GONE : View.VISIBLE);
        Global.SetTouchView(img_title_left, layout_sale_action, layout_remain_time, img_title_right, ll_customer, ll_contact);
        if (null != mCustomer && isCustom) {
            getDefaultContact(mCustomer.contacts);
            tv_customer.setText(mCustomer.name);
            ll_customer.setVisibility(View.GONE);
        } else if (null != mClue && !isCustom) {
            tv_clue_company.setText(mClue.companyName);
            tv_clue_name.setText(mClue.responsorName);
            ll_clue_company.setVisibility(View.GONE);
        }
        controller.loadView(gridView);
        initMultiFunctionModule();
    }

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        final MultiFunctionModule mfmodule = new MultiFunctionModule(this);
        /*录音*/
        mfmodule.setRecordClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecordUtils.permissionRecord()) {
                    if (ll_record.getChildCount() >= 3) {
                        Toast("最多只能添加3条语音");
                        return;
                    }
                    if ((boolean) v.getTag()) {
                        showInputKeyboard(edt);
                        mfmodule.setIsRecording(false);
                        v.setTag(false);
                    } else {
                        hideInputKeyboard(edt);
                        mfmodule.setIsRecording(true);
                        v.setTag(true);
                    }
                } else {
                    Toast("你没有配置录音或者储存权限");
                }

            }
        });
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mfmodule.setIsRecording(false);
            }
        });
        /*录音完成回调*/
        mfmodule.setRecordComplete(new MultiFunctionModule.RecordComplete() {
            @Override
            public void recordComplete(String recordPath, String tiem) {
                ll_record.addView(new CommonRecordItem(DynamicAddActivity.this, recordPath, tiem, uuid, new CommonRecordItem.RecordUploadingCallback() {
                    @Override
                    public void Success(Record record) {//上传录音完成回调
                        audioInfo.add(record);
                    }

                    @Override
                    public void deleteRecord(String tag) {
                        for (int i = 0; i < audioInfo.size(); i++) {
                            Record ele = audioInfo.get(i);
                            if (ele.fileName.contains(tag)) {
                                audioInfo.remove(i);
                            }
                        }
                    }
                }));
            }
        });
        /*图片select*/
        mfmodule.setPictureClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9 - controller.count())
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(DynamicAddActivity.this);
            }
        });
        /*添加地址处理*/
        mfmodule.setLocationClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                app.startActivityForResult(DynamicAddActivity.this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
            }
        });
        /*@相关人员*/
        mfmodule.setAtClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
                if (collection != null) {
                    bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                }
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                Intent intent = new Intent();
                intent.setClass(DynamicAddActivity.this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mfmodule.setEnableModle(true, true, true, true);
        ll_root.addView(mfmodule);
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
     * 提交新建跟进线索:
     * company_id
     * sealslead_id
     * contact_name (姓名)
     * <p>
     * 跟进：
     * company_id
     * customer_id
     * contact_name (联系人)
     */
    public void commitDynamic() {
        HashMap<String, Object> map = new HashMap<>();
        if (isCustom) {
            map.put("customerId", mCustomer.getId());
        } else {
            map.put("sealsleadId", mClue.id);
        }
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
        }
        map.put("contactName", contactName);
        map.put("audioInfo", audioInfo);//上传录音相关
        map.put("location", location);//添加定位相关
        map.put("atDepts", atDepts);
        map.put("atUserIds", atUserIds);

        LogUtil.dee("新建跟进:" + MainApp.gson.toJson(map));

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSaleactivity(map, new RCallback<SaleActivity>() {
            @Override
            public void success(final SaleActivity saleActivity, final Response response) {
                HttpErrorCheck.checkResponse("新建跟进动态", response);
                AppBus.getInstance().post(new FollowUpRushEvent());
                app.finishActivity(DynamicAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
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
                } else if (isCustom) {
                    if (null == mCustomer || TextUtils.isEmpty(mCustomer.getId())) {
                        Toast("请选择跟进客户");
                        return;
                    }
                } else if (!isCustom) {
                    if (null == mClue || TextUtils.isEmpty(mClue.id)) {
                        Toast("请选择跟进线索");
                        return;
                    }
                }

                showLoading("");
                controller.startUpload();
                controller.notifyCompletionIfNeeded();

                break;

            /*选择客户*/
            case R.id.ll_customer:
                Bundle b = new Bundle();
                app.startActivityForResult(DynamicAddActivity.this, SigninSelectCustomerSearch.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                break;

            /*选择联系人*/
            case R.id.ll_contact:
                Bundle bContact = new Bundle();
                bContact.putSerializable(ExtraAndResult.EXTRA_DATA, mCustomer.contacts);
                bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                app.startActivityForResult(DynamicAddActivity.this, FollowContactSelectActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                break;

            /*选择图片*/
            case R.id.layout_image:
                PhotoPicker.builder()
                        .setPhotoCount(9 - controller.count())
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(this);
            /*清除选择的定位信息*/
            case R.id.iv_location_delete:
                ll_location.setVisibility(View.GONE);
                location = null;
                break;
            /*清除  @ 的相关人员*/
            case R.id.iv_at_delete:
                ll_at.setVisibility(View.GONE);
                atDepts.clear();
                atUserIds.clear();
                collection = null;
                break;
            /*线索写跟进选择线索*/
            case R.id.ll_clue_company:
                Bundle bCule = new Bundle();
                bCule.putInt(ExtraAndResult.EXTRA_TYPE, ClueTypeEnum.myCule.getType());
                bCule.putBoolean("isSelect", true);
                bCule.putBoolean("isResult", true);
                app.startActivityForResult(DynamicAddActivity.this, ClueSearchActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, bCule);
//                app.startActivity(DynamicAddActivity.this, ClueSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bCule);
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
                ll_contact.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
//                ll_customer.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
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
                if (data != null) {
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        controller.removeTaskAt(index);
                        controller.reloadGridView();
                    }
                }
                break;
            /*添加地址回调*/
            case MapModifyView.SERACH_MAP:
                PositionResultItem positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    ll_location.setVisibility(View.VISIBLE);
                    tv_location_text.setText(positionResultItem.address);
                    List<Double> loc = new ArrayList<>();
                    loc.add(positionResultItem.laPosition);
                    loc.add(positionResultItem.loPosition);
                    location = new Location(loc, positionResultItem.address);
                }
                break;
            case ExtraAndResult.REQUEST_EDIT:
                mClue = (ClueListItem) data.getSerializableExtra(ClueListItem.class.getName());
                contactName = mClue.responsorName;
                tv_clue_company.setText(mClue.companyName);
                tv_clue_name.setText(mClue.responsorName);
                break;
        }
    }

    /**
     * @相关人员选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        atDepts.clear();
        atUserIds.clear();
        if (FinalVariables.PICK_INVOLVE_USER_REQUEST.equals(event.request)) {
            String atText = "";
            collection = event.data;
            if (collection.depts.size() > 0) {
                for (StaffMember ele : collection.depts) {
                    atDepts.add(new CommonIdName(ele.id, ele.name));
                    atText += ele.name + ",";
                }
            }
            if (collection.users.size() > 0) {
                for (StaffMember ele : collection.users) {
                    atUserIds.add(ele.id);
                    atText += ele.name + ",";
                }
            }
            if (!TextUtils.isEmpty(atText)) {
                ll_at.setVisibility(View.VISIBLE);
                tv_at_text.setText("@" + atText);
            } else {
                ll_at.setVisibility(View.GONE);
            }
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
            if (path.startsWith("file://")) ;
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
