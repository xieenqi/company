package com.loyo.oa.v2.activityui.followup;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.click.NoDoubleClickListener;
import com.loyo.oa.common.type.LoyoBizType;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.clue.common.ClueType;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.commonview.CommonRecordItem;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.contact.ContactsRoleSingleSelectActivity;
import com.loyo.oa.v2.activityui.contact.model.ContactsRoleModel;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.customer.FollowContactSelectActivity;
import com.loyo.oa.v2.activityui.customer.CustomerSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.followup.event.FollowUpRushEvent;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.CommonIdName;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【新建跟进】客户管理
 * <p/>
 * Create by yyy on 16/08/24
 */
public class FollowAddActivity extends BaseActivity implements UploadControllerCallback {

    private static final int RECORD_REQUEST = 0x10;//获取录音需要的权限
    private final static String REQUEST_AT_SELECT = "com.loyo.FollowAddActivity.REQUEST_AT_SELECT";

    private static final int REQUEST_ACTIVITY_CODE_ROLE=0x100;

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private ImageUploadGridView gridView;
    UploadController controller;
    private LinearLayout ll_root, ll_record, ll_location, ll_at, ll_clue_company, ll_clue, ll_customer_holder, ll_clue_holer,ll_contact_label,ll_contact_status,ll_contact_role;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time, tv_customer, tv_contact_name, tv_location_text,
            tv_at_text, tv_clue_company, tv_clue_name,tv_contact_role,tv_contact_label;
    private Customer mCustomer;
    private ClueListItem mClue;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_customer, ll_contact;
    private ImageView iv_location_delete, iv_at_delete;
    private String content;
    private String uuid = StringUtil.getUUID();
    private List<String> mSelectPath;
    private ArrayList<AttachmentBatch> attachment = new ArrayList<>();
    private ArrayList<Record> audioInfo = new ArrayList<>();//录音数据
    private Location location;//添加的定位信息数据
    private List<CommonIdName> atDepts = new ArrayList<>();//@的部门
    private List<String> atUserIds = new ArrayList<>();//@的人员
    private StaffMemberCollection collection;//选人返回的数据
    private boolean isCustom, isRecordRun;//是否是客户写跟进 否则就是是线索写跟进

    private View view;//用来处理权限动态申请
    private MultiFunctionModule mfmodule;

    private long nextFollowUpTime = 0;//下次跟进时间
    private ContactsRoleModel contactsRoleModel;//客户角色
    private int action;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_activities_add);

        controller = new UploadController(this, 9);
        controller.setObserver(this);
        getIntentData();
        getTempSaleActivity();
        initUI();
        if (action == ExtraAndResult.DYNAMIC_ADD_CULE) {
            isCustom = false;
            contactName = mClue.responsorName;
            //显示跟进
            ll_clue_holer.setVisibility(View.VISIBLE);
            initData();
        } else {
            isCustom = true;
            //显示客户
            ll_customer_holder.setVisibility(View.VISIBLE);
            getData();
        }
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
            mClue = (ClueListItem) bundle.getSerializable(ClueListItem.class.getName());
            action = bundle.getInt(ExtraAndResult.DYNAMIC_ADD_ACTION, 0);

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
        img_title_left.setOnClickListener(click);
        layout_sale_action.setOnClickListener(click);
        layout_remain_time.setOnClickListener(click);
        img_title_right.setOnClickListener(click);
        ll_customer.setOnClickListener(click);
        ll_contact.setOnClickListener(click);
        iv_location_delete.setOnClickListener(click);
        iv_at_delete.setOnClickListener(click);
        ll_clue_company.setOnClickListener(click);
        ll_clue_company.setVisibility(isCustom ? View.GONE : View.VISIBLE);
        ll_clue.setVisibility(isCustom ? View.GONE : View.VISIBLE);

        //联系人和线索的容器
        ll_customer_holder = (LinearLayout) findViewById(R.id.ll_customer_holder);
        ll_clue_holer = (LinearLayout) findViewById(R.id.ll_clue_holder);
        //客户标签和状态
        ll_contact_label = (LinearLayout) findViewById(R.id.ll_contact_label);
        ll_contact_status = (LinearLayout) findViewById(R.id.ll_contact_status);
        tv_contact_label=(TextView) findViewById(R.id.tv_contact_label);
        //联系人
        ll_contact_role= (LinearLayout) findViewById(R.id.ll_contact_role);
        tv_contact_role= (TextView) findViewById(R.id.tv_contact_role);
        ll_contact_role.setOnClickListener(click);

        Global.SetTouchView(img_title_left, layout_sale_action, layout_remain_time, img_title_right, ll_customer, ll_contact);

        controller.loadView(gridView);
        initMultiFunctionModule();

    }

    /**
     * 显示数据
     */
    private void initData(){
        if (null != mCustomer && isCustom) {
            //设置默认联系人
            getDefaultContact(mCustomer.contacts);
            //设置客户名称
            tv_customer.setText(mCustomer.name);
            tv_contact_label.setText(Utils.getTagItems(mCustomer));

        } else if (null != mClue && !isCustom) {
            tv_clue_company.setText(mClue.companyName);
            tv_clue_name.setText(mClue.responsorName);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (RECORD_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    startRecord(view);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了所需权限，不能完成操作");
                }
            });
        }
    }

    //开始录音
    private void startRecord(View v) {
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
    }


    /**
     * 获取客户信息
     */
    private void getData(){
        showLoading2("");
        CustomerService.getCustomerDetailById(mCustomer.id)
                .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                    @Override
                    public void onNext(Customer customer) {
                        mCustomer=customer;
                        initData();
                    }
                });
    }

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        mfmodule = new MultiFunctionModule(this);
        /*录音*/
        mfmodule.setRecordClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view = v;
                if (PermissionTool.requestPermission(FollowAddActivity.this, new String[]{
                                Manifest.permission.RECORD_AUDIO, //录音权限
                                Manifest.permission.READ_PHONE_STATE,//读取设备权限
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,//写入外存权限
                                Manifest.permission.READ_EXTERNAL_STORAGE}//读取外存权限
                        , "麦克风或者存储权限被禁用", RECORD_REQUEST)) {

                    startRecord(v);
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
                isRecordRun = true;
                ll_record.addView(new CommonRecordItem(FollowAddActivity.this, recordPath, tiem, uuid, new CommonRecordItem.RecordUploadingCallback() {
                    @Override
                    public void Success(Record record) {//上传录音完成回调
                        audioInfo.add(record);
                        isRecordRun = false;
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
                if (controller.count() >= 9) {
                    Toast("最多选九张图片");
                    return;
                }
                PhotoPicker.builder()
                        .setPhotoCount(9 - controller.count())
                        .setShowCamera(true)
                        .setPreviewEnabled(false)
                        .start(FollowAddActivity.this);
            }
        });
        /*添加地址处理*/
        mfmodule.setLocationClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocationUtilGD.permissionLocation(FollowAddActivity.this)) {
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("page", MapModifyView.SIGNIN_PAGE);
                    app.startActivityForResult(FollowAddActivity.this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                }
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
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, REQUEST_AT_SELECT);
                Intent intent = new Intent();
                intent.setClass(FollowAddActivity.this, ContactPickerActivity.class);
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
            attachmentBatch.bizType = LoyoBizType.FollowUp.getCode();
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
        if (0 != nextFollowUpTime) {
            map.put("remindAt", nextFollowUpTime);
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
        CustomerService.addSaleactivity(map)
                .subscribe(new DefaultLoyoSubscriber<SaleActivity>(hud) {
                    @Override
                    public void onNext(SaleActivity saleActivity) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AppBus.getInstance().post(new FollowUpRushEvent());
                                app.finishActivity(FollowAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }
                        }, 2000);

                    }
                });
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        buildAttachment();
        AttachmentService.setAttachementData(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<AttachmentForNew>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<AttachmentForNew> news) {
                        commitDynamic();
                    }
                });
    }

    /**
     * 获取客户的默认联系人
     *
     * @param data
     */
    private void getDefaultContact(ArrayList<Contact> data) {
        //先清除数据，在用现在的覆盖。
        contactId = "";
        contactName ="";
        tv_contact_name.setText("");
        tv_contact_role.setText("");

        for (Contact ele : data) {
            if (!ele.isDefault()) {
                continue;
            } else {
                contactId = ele.getId();
                contactName = ele.getName();
                tv_contact_name.setText(contactName);
                tv_contact_role.setText("角色名字");
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
//                String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
//                tv_remain_time.setText(str);
                nextFollowUpTime = com.loyo.oa.common.utils.DateTool.getStamp(year, month, day, hour, min, 0);
                tv_remain_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(nextFollowUpTime));

            }

            @Override
            public void onCancel() {
                tv_remain_time.setText("不提醒");
                nextFollowUpTime = 0;
            }
        }, false, "不提醒");
    }


    NoDoubleClickListener click = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                //选择联系人角色
                case R.id.ll_contact_role:
                    app.startActivityForResult(FollowAddActivity.this, ContactsRoleSingleSelectActivity.class, app.ENTER_TYPE_RIGHT, REQUEST_ACTIVITY_CODE_ROLE, null);
                    break;
            /*返回*/
                case R.id.img_title_left:
                    app.finishActivity(FollowAddActivity.this, MainApp.ENTER_TYPE_LEFT, 0, null);
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
                    app.startActivityForResult(FollowAddActivity.this, CommonTagSelectActivity_.class, app.ENTER_TYPE_RIGHT, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
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
                    if (isRecordRun) {
                        Toast("录音上传中稍后提交");
                        return;
                    }

                    showCommitLoading();
                    controller.startUpload();
                    controller.notifyCompletionIfNeeded();

                    break;

            /*选择客户*/
                case R.id.ll_customer:
                    Bundle b = new Bundle();
                    b.putInt(CustomerSearchOrPickerActivity.EXTRA_TYPE, 5);
                    b.putBoolean(CustomerSearchOrPickerActivity.EXTRA_LOAD_DEFAULT, true);
                    app.startActivityForResult(FollowAddActivity.this, CustomerSearchOrPickerActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                    break;

            /*选择联系人*/
                case R.id.ll_contact:
                    Bundle bContact = new Bundle();
                    bContact.putSerializable(ExtraAndResult.EXTRA_DATA, mCustomer.contacts);
                    bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                    app.startActivityForResult(FollowAddActivity.this, FollowContactSelectActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                    break;
            /*选择图片*/
                case R.id.layout_image:
                    PhotoPicker.builder()
                            .setPhotoCount(9 - controller.count())
                            .setShowCamera(true)
                            .setPreviewEnabled(false)
                            .start(FollowAddActivity.this);
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
                    bCule.putSerializable(ClueSearchOrPickerActivity.EXTRA_TYPE, ClueType.MY_CLUE);
                    app.startActivityForResult(FollowAddActivity.this, ClueSearchOrPickerActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_EDIT, bCule);
                    break;
            }
        }
    };


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
            case REQUEST_ACTIVITY_CODE_ROLE:

                break;
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
                    getData();
                }
                tv_customer.setText(customerName);
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
                mClue = (ClueListItem) data.getSerializableExtra("data");
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
        if (REQUEST_AT_SELECT.equals(event.request)) {
            atDepts.clear();
            atUserIds.clear();

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
        int count = controller.failedTaskCount();
        if (count > 0) {
            cancelCommitLoading();
            LoyoToast.info(this, count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            commitDynamic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < ll_record.getChildCount(); i++) {
            ((CommonRecordItem) ll_record.getChildAt(i)).cleanPlayRecord();
        }
    }

}
