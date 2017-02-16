package com.loyo.oa.v2.activityui.signin;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.type.LoyoBizType;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoCapture;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.CommonRecordItem;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.contact.ContactsRoleSingleSelectActivity;
import com.loyo.oa.v2.activityui.contact.model.ContactsRoleModel;
import com.loyo.oa.v2.activityui.customer.FollowContactSingleSelectActivity;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
import com.loyo.oa.v2.activityui.signin.event.SigninCustomerRushEvent;
import com.loyo.oa.v2.activityui.signin.event.SigninRushEvent;
import com.loyo.oa.v2.activityui.signin.presenter.SigninPresenterImpl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.CommonIdName;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hk.ids.gws.android.sclick.SClick;


/**
 * 【 拜访签到 】 页面
 */
public class SignInActivity extends BaseActivity
        implements SigninContract.View, UploadControllerCallback {
    private static final int RECORD_REQUEST = 0x10;//获取录音需要的权限
    private static final int REQUEST_RESULT_ROLE = 0x10;//获取联系人角色

    private TextView tv_customer_name, tv_reset_address, tv_address, wordcount, tv_customer_address,
            tv_at_text, tv_distance_deviation, tv_contact_name, tv_contact_role;
    private EditText edt_memo;
    private ViewGroup img_title_left, img_title_right, ll_root, ll_record, ll_at, ll_contact_holder, ll_contact_name, ll_contact_role;
    private ViewGroup layout_customer_name;
    private ImageView iv_at_delete, iv_customer_name;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private String uuid = StringUtil.getUUID();
    private double laPosition, loPosition;
    boolean mLocationFlag = false;  //是否定位完成的标记
    private Customer mCustomer;
    private Animation animation;
    private boolean isPicture = false, isCusPosition = false, isLocation = false, isRecordRun = false;
    private PositionResultItem positionResultItem;
    private StaffMemberCollection collection;//选人返回的数据
    private ArrayList<Record> audioInfo = new ArrayList<>();//录音数据
    private List<CommonIdName> atDepts = new ArrayList<>();//@的部门
    private List<String> atUserIds = new ArrayList<>();//@的人员
    private SigninContract.Presenter presenter;
    UploadController controller;
    ImageUploadGridView gridView;
    private View view;
    private MultiFunctionModule mfmodule;
    public String cityCode;
    public String message;
    public String region;//地区
    private Contact contact;
    private Location loc;//定位地址

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.setTitle("拜访签到");
        LocationUtilGD.permissionLocation(this);
        presenter = new SigninPresenterImpl(this);
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        presenter.getIsPhoto();
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra("data")) {
            mCustomer = (Customer) intent.getSerializableExtra("data");
        }
        initUI();
        startLocation();
    }

    /**
     * 进来还是要先加载联系人数据，主要是为了加载联系人角色
     */
    public void getData(String customerId) {
        showLoading2("");
        CustomerService.getCustomerDetailById(customerId)
                .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                    @Override
                    public void onError(Throwable e) {
                        hud.dismiss();
                        Toast("网络异常，请重试");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Customer customer) {
                        mCustomer = customer;
                        initData();
                    }
                });
    }

    void initUI() {
        ll_contact_holder = (ViewGroup) findViewById(R.id.ll_contact_holder);
        ll_contact_name = (ViewGroup) findViewById(R.id.ll_contact_name);
        ll_contact_role = (ViewGroup) findViewById(R.id.ll_contact_role);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        tv_contact_role = (TextView) findViewById(R.id.tv_contact_role);
        tv_customer_address = (TextView) findViewById(R.id.tv_customer_address);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(click);
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_reset_address = (TextView) findViewById(R.id.tv_reset_address);
        tv_reset_address.setOnTouchListener(Global.GetTouch());
        tv_reset_address.setOnClickListener(click);
        edt_memo = (EditText) findViewById(R.id.edt_memo);
        wordcount = (TextView) findViewById(R.id.wordcount);
        edt_memo.addTextChangedListener(new CountTextWatcher(wordcount));
        ll_root = (ViewGroup) findViewById(R.id.ll_root);
        ll_record = (ViewGroup) findViewById(R.id.ll_record);
        ll_at = (ViewGroup) findViewById(R.id.ll_at);
        tv_at_text = (TextView) findViewById(R.id.tv_at_text);
        tv_distance_deviation = (TextView) findViewById(R.id.tv_distance_deviation);
        layout_customer_name = (ViewGroup) findViewById(R.id.layout_customer_name);
        ll_contact_name.setOnClickListener(click);
        ll_contact_role.setOnClickListener(click);
        tv_address = (TextView) findViewById(R.id.tv_address);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        iv_at_delete = (ImageView) findViewById(R.id.iv_at_delete);
        iv_customer_name = (ImageView) findViewById(R.id.iv_customer_name);
        iv_at_delete.setOnClickListener(click);
        initMultiFunctionModule();
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);
        if (null == mCustomer) {
            //为空就可以选择客户，如果不为空，所以已经指定了客户，不允许修改
            layout_customer_name.setOnTouchListener(Global.GetTouch());
            layout_customer_name.setOnClickListener(click);
        } else {
            //选择了客户，直接加载数据
            iv_customer_name.setVisibility(View.GONE);
            tv_customer_name.setPadding(tv_customer_name.getPaddingLeft(), tv_customer_name.getPaddingTop(), DensityUtil.dp2px(this, 23), tv_customer_name.getPaddingBottom());

        }

    }

    private void initData() {
//        findViewById(R.id.divider_customer_name).setVisibility(View.VISIBLE);
        tv_customer_name.setText(mCustomer.name);
        ll_contact_holder.setVisibility(View.VISIBLE);
        tv_customer_address.setVisibility(View.VISIBLE);
        tv_customer_address.setText((null == mCustomer.loc || TextUtils.isEmpty(mCustomer.loc.addr)) ? "未知地址" : mCustomer.loc.addr);
        contact = presenter.getDefaultContact(mCustomer.contacts);
        if (null != contact) {
            tv_contact_name.setText(contact.getName());
            tv_contact_role.setText(contact.getContactRoleName());
        }
        edt_memo.setText(TextUtils.isEmpty(mCustomer.name) ? "" : "我拜访了" + mCustomer.name);
        //此处是客户详情在定位成功过后再计算偏差
        if (mCustomer != null && mCustomer.position != null) {
            List<Double> locList = new ArrayList<>();
            for (Double ele : mCustomer.position.loc) {
                locList.add(ele);
            }
            loc = new Location(locList, mCustomer.position.addr);
        }
        //显示到客户的距离
        distanceInfo();

    }

    void startLocation() {
        mLocationFlag = false;
        new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                animation.reset();
                laPosition = latitude;
                loPosition = longitude;
                app.address = address;
                tv_address.setText(address);
                cityCode = app.cityCode;
                message = app.message;
                region = app.region;
                LocationUtilGD.sotpLocation();
                UMengTools.sendLocationInfo(address, longitude, latitude);
                getData(mCustomer.getId());
            }

            @Override
            public void OnLocationGDFailed() {
                animation.reset();
                Toast(R.string.LOCATION_FAILED);
                boolean gpsOpen = Utils.isGPSOPen(mContext);
                if (!gpsOpen) {
                    Global.ToastLong("建议开启GPS,重新定位");
                }

                LocationUtilGD.sotpLocation();
            }
        });
    }


    private void startRecord(View view) {
        if ((boolean) view.getTag()) {
            showInputKeyboard(edt_memo);
            mfmodule.setIsRecording(false);
            view.setTag(false);
        } else {
            hideInputKeyboard(edt_memo);
            mfmodule.setIsRecording(true);
            view.setTag(true);
        }
        UmengAnalytics.umengSend(SignInActivity.this, UmengAnalytics.addVisitRecord);
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

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        mfmodule = new MultiFunctionModule(this);
        ll_root.addView(mfmodule);
        mfmodule.setEnableModle(true, true, false, true);
        /*录音*/
        mfmodule.setRecordClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_record.getChildCount() >= 3) {
                    Toast("最多只能添加3条语音");
                    return;
                }
                view = v;
                if (PermissionTool.requestPermission(SignInActivity.this, new String[]{
                                Manifest.permission.RECORD_AUDIO, //录音权限
                                Manifest.permission.READ_PHONE_STATE,//读取设备权限
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,//写入外存权限
                                Manifest.permission.READ_EXTERNAL_STORAGE}//读取外存权限
                        , "麦克风或者存储权限被禁用", RECORD_REQUEST)) {
                    startRecord(v);
                }

            }
        });
        edt_memo.setOnClickListener(new View.OnClickListener() {
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
                ll_record.addView(new CommonRecordItem(SignInActivity.this, recordPath, tiem, uuid, new CommonRecordItem.RecordUploadingCallback() {
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
        /*图片处理*/
        mfmodule.setPictureClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.count() >= 9) {
                    Toast("最多只能加9张图片");
                } else {
                    PhotoCapture.builder()
                            .start(SignInActivity.this);
                    UmengAnalytics.umengSend(SignInActivity.this, UmengAnalytics.addVisitPhoto);
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
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                Intent intent = new Intent();
                intent.setClass(SignInActivity.this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                UmengAnalytics.umengSend(SignInActivity.this, UmengAnalytics.addVisitAt);
            }
        });
        mfmodule.setPictureIcon(R.drawable.icon_picture_photo);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.img_title_left:
                    onBackPressed();
                    break;

                case R.id.img_title_right:
                    if (null == mCustomer) {
                        Toast("请选择客户");
                        return;
                    }
                    if (!checkData()) {
                        return;
                    }
                    if (!isLocation) {
                        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dismissSweetAlert();
                                sendSigninData();
                            }
                        }, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                isCusPosition = true;
                                dismissSweetAlert();
                                sendSigninData();
                            }
                        }, "提示", "该客户无定位信息,是否需要\n将签到地址设置为客户定位?", "不需要", "设为定位");
                    } else {
                        sendSigninData();
                    }
                    break;

            /*选择客户*/
                case R.id.layout_customer_name:
                    if (LocationUtilGD.permissionLocation(SignInActivity.this)) {
                        Bundle b = new Bundle();
                        b.putDouble("lon", loPosition);
                        b.putDouble("lat", laPosition);
                        app.startActivity(SignInActivity.this, SigninSelectCustomerActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                    }
                    break;

            /*地址更新*/
                case R.id.tv_reset_address:
                    //避免其他地方定位影响
                    app.longitude = loPosition;
                    app.latitude = laPosition;
                    app.address = tv_address.getText().toString();
                    app.region = region;
                    app.cityCode = cityCode;
                    app.message = message;
                    if (LocationUtilGD.permissionLocation(SignInActivity.this)) {
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("page", MapModifyView.SIGNIN_PAGE);
                        app.startActivityForResult(SignInActivity.this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                    }
                    break;
                //选择客户联系人
                case R.id.ll_contact_name:
                    if (null == mCustomer) {
                        Toast("请先选择客户");
                        return;
                    }
                    Bundle bContact = new Bundle();
                    bContact.putSerializable(FollowContactSingleSelectActivity.EXTRA_DATA, mCustomer.contacts);
                    bContact.putString(FollowContactSingleSelectActivity.EXTRA_CURRENT, null == contact ? null : contact.getId());
                    app.startActivityForResult(SignInActivity.this, FollowContactSingleSelectActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                    break;
                //选择客户联系人角色
                case R.id.ll_contact_role:
                    if (null == contact) {
                        Toast("请先选择联系人");
                        return;
                    }
                    Bundle bb = new Bundle();
                    bb.putSerializable(ContactsRoleSingleSelectActivity.EXTRA_CURRENT, contact.getContactRoleId());//当前选中的
                    app.startActivityForResult(SignInActivity.this, ContactsRoleSingleSelectActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, REQUEST_RESULT_ROLE, bb);
                    break;

                case R.id.iv_at_delete://清除@的人员
                    ll_at.setVisibility(View.GONE);
                    atDepts.clear();
                    atUserIds.clear();
                    collection = null;
                    break;
            }
        }
    };

    private boolean checkData() {
        if (TextUtils.isEmpty(mCustomer.getId())) {
            Toast("请选择客户");
            return false;
        }
        if (TextUtils.isEmpty(tv_address.getText().toString())) {
            Global.ToastLong("无效地址!请刷新地址后重试");
            return false;
        }
        if (TextUtils.isEmpty(edt_memo.getText().toString())) {
            Global.ToastLong("请填写拜访说明");
            return false;
        }
        if (isPicture && controller.count() <= 0) {
            Global.ToastLong("需要上传照片，请拍照");
            return false;
        }
        if (isRecordRun) {
            Toast("录音上传中稍后提交");
            return false;
        }
        return true;
    }

    private void sendSigninData() {
        showCommitLoading();
        if (controller.count() == 0) {
            addSignIn();
        } else {
            controller.startUpload();
            controller.notifyCompletionIfNeeded();
        }
    }

    /**
     * 新增签到
     */
    private void addSignIn() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("gpsInfo", loPosition + "," + laPosition);//当前定位信息
//      map.put("address", mAddress.trim());//客户地址
        map.put("position", tv_address.getText().toString());//当前定位地址
        map.put("attachmentUUId", uuid);
        map.put("customerId", mCustomer.getId());
        map.put("audioInfo", audioInfo);
        map.put("atDepts", atDepts);
        map.put("atUserIds", atUserIds);
        map.put("contactName", tv_contact_name.getText().toString());
        map.put("isCusPosition", isCusPosition);//是否把签到地址设为客户定位地址
        if (!StringUtil.isEmpty(edt_memo.getText().toString())) {
            map.put("memo", edt_memo.getText().toString());
        }
        if (null != contact) {
            map.put("contactRoleId", contact.getContactRoleId());//用户角色id
        }
        if (null != mCustomer && null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
            if (null != mCustomer.contacts.get(0).telGroup && mCustomer.contacts.get(0).telGroup.size() > 0) {
                map.put("contactTpl", mCustomer.contacts.get(0).telGroup.get(0));
            }
        }
        if (!SClick.check(SClick.BUTTON_CLICK, 5000)) {
            return;
        }

        LogUtil.d(" 新增拜访传递数据：" + MainApp.gson.toJson(map));
        presenter.creatSignin(map);
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

    /**
     * 获取附件
     */
    private void getAttachments() {
        presenter.getAttachment(uuid);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            //联系人角色
            case REQUEST_RESULT_ROLE:
                ContactsRoleModel contactsRoleModel = (ContactsRoleModel) data.getSerializableExtra("data");
                if (null == contactsRoleModel || RESULT_OK != resultCode) return;
                contact.setContactRoleId(contactsRoleModel.id);
                contact.setContactRoleName(contactsRoleModel.name);
                tv_contact_role.setText(contactsRoleModel.name);
                break;
            //地图微调，数据回到
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    tv_address.setText(positionResultItem.address);
                    distanceInfo();
                }
                break;

            /*相册选择 回调*/
            case PhotoCapture.REQUEST_CODE:
                /*相册选择 回调*/
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
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
             /* 选择客户联系人 回调*/
            case ExtraAndResult.REQUEST_CODE_STAGE:
                contact = (Contact) data.getSerializableExtra("data");
                if (null != contact) {
                    tv_contact_name.setText(contact.getName());
                    tv_contact_role.setText(contact.getContactRoleName());
                }
                break;
        }
    }


    /**
     * 通过EventBus处理选择客户
     */
    @Subscribe
    public void onSigninCustomerRushEvent(SigninCustomerRushEvent event) {
        //TODO  此处 临时处理 后期统一客户数据传递
        mCustomer = new Customer();
        mCustomer.id = event.data.id;
        startLocation();
    }


    private void distanceInfo() {
        //此处是客户详情在定位成功过后再计算偏差
        if (0D == laPosition && 0D == loPosition) {
            tv_distance_deviation.setText("未知");
            tv_distance_deviation.setTextColor(Color.parseColor("#f5625a"));
            isLocation = false;
        }
        if (mCustomer != null && mCustomer.position != null && null != mCustomer.position.loc) {
            List<Double> locList = new ArrayList<>();
            for (Double ele : mCustomer.position.loc) {
                locList.add(ele);
            }
            loc = new Location(locList, mCustomer.position.addr);
            if (loc.loc != null && loc.loc.size() > 0 && loc.loc.get(0) > 0) {
                tv_distance_deviation.setText(presenter.getDeviationDistance(loc.loc.get(0), loc.loc.get(1), laPosition, loPosition));
                tv_distance_deviation.setTextColor(Color.parseColor("#666666"));
                isLocation = true;
            } else {
                tv_distance_deviation.setText("未知");
                tv_distance_deviation.setTextColor(Color.parseColor("#f5625a"));
                isLocation = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 0; i < ll_record.getChildCount(); i++) {
            ((CommonRecordItem) ll_record.getChildAt(i)).cleanPlayRecord();
        }

    }

    @Override
    protected void onDestroy() {
        AppBus.getInstance().unregister(this);
        super.onDestroy();
        presenter.destory();
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public LoyoProgressHUD getHUD() {
        return hud;
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this, message);
    }

    public SignInActivity() {

    }

    @Override
    public void setIsPhoto(boolean isPicture) {
        this.isPicture = isPicture;
    }

    @Override
    public void creatSuccessUI(final LegWork legWork) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(legWork.getId())) {
                    //更新客户状态信息
                    MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent(mCustomer);
                    myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_UPDATE;
                    myCustomerRushEvent.subCode = MyCustomerRushEvent.EVENT_SUB_CODE_LTC;
                    myCustomerRushEvent.session = mCustomer.getId();
                    myCustomerRushEvent.request = "note";
                    AppBus.getInstance().post(myCustomerRushEvent);
                    //更新签到
                    AppBus.getInstance().post(new SigninRushEvent());
                    onBackPressed();
                }
            }
        }, 2000);
    }

    @Override
    public void uploadAttachmentSuccessUI() {
        getAttachments();
    }

    @Override
    public void getAttachmentSuccessUI(ArrayList<Attachment> attachments) {
        lstData_Attachment = attachments;
    }

    @Override
    public void deleteAttachmentSuccessUI(Attachment delAttachment) {
        Toast("删除附件成功!");
        lstData_Attachment.remove(delAttachment);
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        ArrayList<UploadTask> list = controller.getTaskList();
        ArrayList<AttachmentBatch> attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = LoyoBizType.CustomerVisit.getCode();/* */
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        addSignIn();
                    }
                });
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {

        PhotoCapture.builder()
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
            addSignIn();
        }
    }
}
