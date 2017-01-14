package com.loyo.oa.v2.activityui.signin;

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

import com.loyo.oa.common.click.NoDoubleClickListener;
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
import com.loyo.oa.v2.activityui.commonview.RecordUtils;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.FollowContactSelectActivity;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.signin.contract.SigninContract;
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
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSearchActivity;
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


/**
 * 【 拜访签到 】 页面
 */
public class SignInActivity extends BaseActivity
        implements SigninContract.View, UploadControllerCallback {

    private TextView tv_customer_name, tv_reset_address, tv_address, wordcount, tv_customer_address,
            tv_at_text, tv_distance_deviation, tv_contact_name;
    private EditText edt_memo;
    private ViewGroup img_title_left, img_title_right, ll_root, ll_record, ll_at, ll_contact;
    private ImageView iv_at_delete;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private String uuid = StringUtil.getUUID(), customerId = "", customerName, customerAddress;
    private double laPosition, loPosition;
    boolean mLocationFlag = false;  //是否定位完成的标记
    private Customer mCustomer;
    private ArrayList<Contact> contactList;
    private Animation animation;
    private boolean isPicture = false, isCusPosition = false, isLocation = false, isRecordRun = false;
    private PositionResultItem positionResultItem;
    private int pcitureNumber;//记录上传了多少张图
    private StaffMemberCollection collection;//选人返回的数据
    private ArrayList<Record> audioInfo = new ArrayList<>();//录音数据
    private List<CommonIdName> atDepts = new ArrayList<>();//@的部门
    private List<String> atUserIds = new ArrayList<>();//@的人员
    private SigninContract.Presenter presenter;
    UploadController controller;
    ImageUploadGridView gridView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.setTitle("拜访签到");
        presenter = new SigninPresenterImpl(this);
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra("data")) {
            mCustomer = (Customer) intent.getSerializableExtra("data");
            customerId = mCustomer.getId();
            customerName = mCustomer.name;
            customerAddress = mCustomer.loc.addr;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        presenter.getIsPhoto();
        initUI();
        LocationUtilGD.permissionLocation(this);
    }

    void initUI() {
        ll_contact = (ViewGroup) findViewById(R.id.ll_contact);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
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
        ViewGroup layout_customer_name = (ViewGroup) findViewById(R.id.layout_customer_name);
        ll_contact.setOnClickListener(click);
        tv_address = (TextView) findViewById(R.id.tv_address);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        iv_at_delete = (ImageView) findViewById(R.id.iv_at_delete);
        iv_at_delete.setOnClickListener(click);
        startLocation();
        initMultiFunctionModule();
        if (null == mCustomer) {
            layout_customer_name.setOnTouchListener(Global.GetTouch());
            layout_customer_name.setOnClickListener(click);
            ll_contact.setVisibility(View.GONE);
        } else {
            findViewById(R.id.divider_customer_name).setVisibility(View.VISIBLE);
            layout_customer_name.setVisibility(View.VISIBLE);
            layout_customer_name.setEnabled(false);
            tv_customer_name.setText(customerName);
            ll_contact.setVisibility(View.VISIBLE);
            tv_customer_address.setVisibility(View.VISIBLE);
            tv_customer_address.setText(TextUtils.isEmpty(customerAddress) ? "未知地址" : customerAddress);
            tv_contact_name.setText(presenter.getDefaultContact(mCustomer.contacts));
            contactList = mCustomer.contacts;
        }
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);
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
                LocationUtilGD.sotpLocation();
                UMengTools.sendLocationInfo(address, longitude, latitude);
                //此处是客户详情在定位成功过后再计算偏差
                if (mCustomer != null && mCustomer.position != null) {
                    List<Double> locList = new ArrayList<>();
                    for (Double ele : mCustomer.position.loc) {
                        locList.add(ele);
                    }
                    Location loc = new Location(locList, mCustomer.position.addr);
                    distanceInfo(loc);
                }
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

    /**
     * 初始化底部多功能部件
     */
    private void initMultiFunctionModule() {
        final MultiFunctionModule mfmodule = new MultiFunctionModule(this);
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

                if (RecordUtils.permissionRecord(this)) {
                    if ((boolean) v.getTag()) {
                        showInputKeyboard(edt_memo);
                        mfmodule.setIsRecording(false);
                        v.setTag(false);
                    } else {
                        hideInputKeyboard(edt_memo);
                        mfmodule.setIsRecording(true);
                        v.setTag(true);
                    }
                    UmengAnalytics.umengSend(SignInActivity.this, UmengAnalytics.addVisitRecord);
                } else {
                    Toast("你没有配置录音或者储存权限");
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
//                    PhotoPicker.builder()
//                            .setPhotoCount(9-controller.count())
//                            .setShowCamera(true)
//                            .setPreviewEnabled(false)
//                            .start(SignInActivity.this);
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

    NoDoubleClickListener click = new NoDoubleClickListener(4000) {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {

                case R.id.img_title_left:
                    onBackPressed();
                    break;

                case R.id.img_title_right:
                    if (!checkData()) {
                        return;
                    }
                    if (!isLocation) {
                        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dismissSweetAlert();
                                addSignIn();
                            }
                        }, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                isCusPosition = true;
                                dismissSweetAlert();
                                addSignIn();
                            }
                        }, "提示", "该客户无定位信息,是否需要\n将签到地址设置为客户定位?", "不需要", "设为定位");
                    } else {
                        showCommitLoading();
                        if (controller.count() == 0) {
                            addSignIn();
                        } else {
                            controller.startUpload();
                            controller.notifyCompletionIfNeeded();
                        }
                    }
                    break;

            /*选择客户*/
                case R.id.layout_customer_name:
                    if (LocationUtilGD.permissionLocation(SignInActivity.this)) {
                        Bundle b = new Bundle();
                        b.putDouble("lon", loPosition);
                        b.putDouble("lat", laPosition);
                        app.startActivityForResult(SignInActivity.this, SigninSelectCustomerActivity.class, MainApp.ENTER_TYPE_RIGHT, BaseSearchActivity.REQUEST_SEARCH, b);
                    }
                    break;

            /*地址更新*/
                case R.id.tv_reset_address:
                    if (LocationUtilGD.permissionLocation(SignInActivity.this)) {
                        Bundle mBundle = new Bundle();
                        mBundle.putInt("page", MapModifyView.SIGNIN_PAGE);
                        app.startActivityForResult(SignInActivity.this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                    }
                    break;

                case R.id.ll_contact://选择客户联系人
                    Bundle bContact = new Bundle();
                    bContact.putSerializable(ExtraAndResult.EXTRA_DATA, contactList);
                    bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                    app.startActivityForResult(SignInActivity.this, FollowContactSelectActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
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
        if (TextUtils.isEmpty(customerId)) {
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

    /**
     * 新增签到
     */
    private void addSignIn() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("gpsInfo", loPosition + "," + laPosition);//当前定位信息
//      map.put("address", mAddress.trim());//客户地址
        map.put("position", tv_address.getText().toString());//当前定位地址
        map.put("attachmentUUId", uuid);
        map.put("customerId", customerId);
        map.put("audioInfo", audioInfo);
        map.put("atDepts", atDepts);
        map.put("atUserIds", atUserIds);
        map.put("contactName", tv_contact_name.getText().toString());
        map.put("isCusPosition", isCusPosition);//是否把签到地址设为客户定位地址
        if (!StringUtil.isEmpty(edt_memo.getText().toString())) {
            map.put("memo", edt_memo.getText().toString());
        }

        if (null != contactList && contactList.size() > 0) {
            if (null != contactList.get(0).telGroup && contactList.get(0).telGroup.size() > 0) {
                map.put("contactTpl", contactList.get(0).telGroup.get(0));
            }
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
            //地图微调，数据回到
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    tv_address.setText(positionResultItem.address);
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
            /*选择客户回调*/
            case BaseSearchActivity.REQUEST_SEARCH:
                customerId = data.getStringExtra("id");
                customerName = data.getStringExtra("name");
                contactList = (ArrayList<Contact>) data.getSerializableExtra("contact");
                Location loc = (Location) data.getSerializableExtra("loc");
                if (loc != null) {
                    customerAddress = loc.addr;
                }
                if (contactList != null && contactList.size() > 0) {
                    ll_contact.setVisibility(View.VISIBLE);
                    tv_contact_name.setText(presenter.getDefaultContact(contactList));
                } else {
                    ll_contact.setVisibility(View.GONE);
                    tv_contact_name.setText("");
                }
                tv_customer_name.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                edt_memo.setText(TextUtils.isEmpty(customerName) ? "" : "我拜访了" + customerName);
                tv_customer_address.setVisibility(View.VISIBLE);
                tv_customer_address.setText(TextUtils.isEmpty(customerAddress) ? "未知地址" : customerAddress);
                distanceInfo(loc);
                break;
             /* 选择客户联系人 回调*/
            case ExtraAndResult.REQUEST_CODE_STAGE:
                Contact contact = (Contact) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                if (null != contact) {
                    tv_contact_name.setText(contact.getName());
                }
                break;
        }
    }

    private void distanceInfo(Location loc) {
        if (loc != null && loc.loc != null && loc.loc.size() > 0 && loc.loc.get(0) > 0) {
            tv_distance_deviation.setText(presenter.getDeviationDistance(loc.loc.get(0), loc.loc.get(1), laPosition, loPosition));
            tv_distance_deviation.setTextColor(Color.parseColor("#666666"));
            isLocation = true;
        } else {
            tv_distance_deviation.setText("未知");
            tv_distance_deviation.setTextColor(Color.parseColor("#f5625a"));
            isLocation = false;
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
        super.onDestroy();
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
                    AppBus.getInstance().post(new SigninRushEvent());
                    onBackPressed();
                }
            }
        }, 2000);
    }

    @Override
    public void uploadAttachmentSuccessUI() {
        getAttachments();
        pcitureNumber++;
    }

    @Override
    public void getAttachmentSuccessUI(ArrayList<Attachment> attachments) {
        lstData_Attachment = attachments;
    }

    @Override
    public void deleteAttachmentSuccessUI(Attachment delAttachment) {
        Toast("删除附件成功!");
        lstData_Attachment.remove(delAttachment);
        pcitureNumber--;
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
            attachmentBatch.bizType = 0/* */;
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
//        PhotoPicker.builder()
//                .setPhotoCount(9-controller.count())
//                .setShowCamera(true)
//                .setPreviewEnabled(false)
//                .start(this);

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
