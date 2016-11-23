package com.loyo.oa.v2.activityui.signin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMember;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.CommonRecordItem;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.MultiFunctionModule;
import com.loyo.oa.v2.activityui.commonview.RecordUtils;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.FollowContactSelectActivity;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.signinnew.event.SigninNewRushEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CommonIdName;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.beans.Record;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CountTextWatcher;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * 【 拜访签到 】 页面
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_customer_name, tv_reset_address, tv_address, wordcount, tv_customer_address,
            tv_at_text, tv_distance_deviation, tv_contact_name;
    private EditText edt_memo;
    private ViewGroup img_title_left, img_title_right, ll_root, ll_record, ll_at, ll_contact;
    private GridView gridView_photo;
    private ImageView iv_at_delete;
    private ArrayList<Attachment> lstData_Attachment = new ArrayList<>();
    private String uuid = StringUtil.getUUID(), mAddress, customerId = "", customerName, customerAddress;
    private SignInGridViewAdapter signInGridViewAdapter;
    private double laPosition, loPosition;
    boolean mLocationFlag = false;  //是否定位完成的标记
    private Customer mCustomer;
    private ArrayList<Contact> contactList;
    private Animation animation;
    private boolean isPicture = false, isCusPosition = false;
    private PositionResultItem positionResultItem;
    private int pcitureNumber;//记录上传了多少张图
    private StaffMemberCollection collection;//选人返回的数据
    private ArrayList<Record> audioInfo = new ArrayList<>();//录音数据
    private List<CommonIdName> atDepts = new ArrayList<>();//@的部门
    private List<String> atUserIds = new ArrayList<>();//@的人员

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        super.setTitle("拜访签到");
        Intent intent = getIntent();
        if (null != intent && intent.hasExtra("data")) {
            mCustomer = (Customer) intent.getSerializableExtra("data");
            customerId = mCustomer.getId();
            customerName = mCustomer.name;
            customerAddress = mCustomer.loc.addr;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        getIsPhoto();
        initUI();
    }

    void initUI() {
        ll_contact = (ViewGroup) findViewById(R.id.ll_contact);
        tv_customer_name = (TextView) findViewById(R.id.tv_customer_name);
        tv_customer_address = (TextView) findViewById(R.id.tv_customer_address);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_reset_address = (TextView) findViewById(R.id.tv_reset_address);
        tv_reset_address.setOnTouchListener(Global.GetTouch());
        tv_reset_address.setOnClickListener(this);
        edt_memo = (EditText) findViewById(R.id.edt_memo);
        wordcount = (TextView) findViewById(R.id.wordcount);
        edt_memo.addTextChangedListener(new CountTextWatcher(wordcount));
        ll_root = (ViewGroup) findViewById(R.id.ll_root);
        ll_record = (ViewGroup) findViewById(R.id.ll_record);
        ll_at = (ViewGroup) findViewById(R.id.ll_at);
        tv_at_text = (TextView) findViewById(R.id.tv_at_text);
        tv_distance_deviation = (TextView) findViewById(R.id.tv_distance_deviation);
        ViewGroup layout_customer_name = (ViewGroup) findViewById(R.id.layout_customer_name);

        ll_contact.setOnClickListener(this);
        tv_address = (TextView) findViewById(R.id.tv_address);
        gridView_photo = (GridView) findViewById(R.id.gridView_photo);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        iv_at_delete = (ImageView) findViewById(R.id.iv_at_delete);
        iv_at_delete.setOnClickListener(this);
        init_gridView_photo();
        startLocation();
        initMultiFunctionModule();
        if (null == mCustomer) {
            layout_customer_name.setOnTouchListener(Global.GetTouch());
            layout_customer_name.setOnClickListener(this);
            ll_contact.setVisibility(View.GONE);
        } else {
            findViewById(R.id.divider_customer_name).setVisibility(View.VISIBLE);
            layout_customer_name.setVisibility(View.VISIBLE);
            layout_customer_name.setEnabled(false);
            tv_customer_name.setText(customerName);
            ll_contact.setVisibility(View.VISIBLE);
            tv_customer_address.setVisibility(View.VISIBLE);
            tv_customer_address.setText(TextUtils.isEmpty(customerAddress) ? "未知地址" : customerAddress);
            getDefaultContact(mCustomer.contacts);
            contactList = mCustomer.contacts;
            if (mCustomer.position != null) {
                List<Double> locList = new ArrayList<>();
                for (Double ele : mCustomer.position.loc) {
                    locList.add(ele);
                }
                Location loc = new Location(locList, mCustomer.position.addr);
                distanceInfo(loc);
            }
        }
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

                if (RecordUtils.permissionRecord()) {
                    if ((boolean) v.getTag()) {
                        showInputKeyboard(edt_memo);
                        mfmodule.setIsRecording(false);
                        v.setTag(false);
                    } else {
                        hideInputKeyboard(edt_memo);
                        mfmodule.setIsRecording(true);
                        v.setTag(true);
                    }
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
                ll_record.addView(new CommonRecordItem(SignInActivity.this, recordPath, tiem, uuid, new CommonRecordItem.RecordUploadingCallback() {
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
        /*图片处理*/
        mfmodule.setPictureClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signInGridViewAdapter.getCount() >= 9) {
                    Toast("最多只能加9张图片");
                } else {
                    Intent intent = new Intent(SignInActivity.this, SelectPicPopupWindow.class);
                    intent.putExtra("localpic", false);//是否可以选择相册
                    intent.putExtra("imgsize", 9 - pcitureNumber);//还可以选多少张图片
                    intent.putExtra("addpg", true);
                    startActivityForResult(intent, MainApp.GET_IMG);
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
            }
        });
    }

    /**
     * 获取签到是否需要传递图片
     */
    private void getIsPhoto() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", "need_pictures_switcher");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSetInfo(map, new Callback<SigninPictures>() {
            @Override
            public void success(SigninPictures result, Response response) {
                HttpErrorCheck.checkResponse("签到时必须操作？？？", response);
                if (result.value.equals("1")) {
                    isPicture = true;
                } else {
                    isPicture = false;
                }
            }

            @Override
            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
            }
        });
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
            }

            @Override
            public void OnLocationGDFailed() {
                animation.reset();
                boolean gpsOpen = Utils.isGPSOPen(mContext);
                if (!gpsOpen) {
                    Global.ToastLong("建议开启GPS,重新定位");
                }
                Toast("定位失败,请在网络和GPS信号良好时重试");
                LocationUtilGD.sotpLocation();
            }
        });
    }

    /**
     * 图片适配器绑定
     */
    void init_gridView_photo() {
        signInGridViewAdapter = new SignInGridViewAdapter(this, lstData_Attachment, true, true, 0);
        SignInGridViewAdapter.setAdapter(gridView_photo, signInGridViewAdapter);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                onBackPressed();
                break;

            case R.id.img_title_right:
                if (TextUtils.isEmpty(customerAddress)) {
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
                            customerAddress = tv_address.getText().toString();
                            addSignIn();
                        }
                    }, "提示", "该客户无定位信息,是否需要\n将签到地址设置为客户定位?", "不需要", "设为定位");
                } else {
                    addSignIn();
                }
                break;

            /*选择客户*/
            case R.id.layout_customer_name:
                Bundle b = new Bundle();//SigninSelectCustomerSearch
                app.startActivityForResult(this, SigninSelectCustomerActivity.class, MainApp.ENTER_TYPE_RIGHT, BaseSearchActivity.REQUEST_SEARCH, b);
                break;

            /*地址更新*/
            case R.id.tv_reset_address:
                Bundle mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.SIGNIN_PAGE);
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
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

    /**
     * 新增签到
     */
    private void addSignIn() {
        if (TextUtils.isEmpty(customerId)) {
            Toast("请选择客户");
            return;
        }
        mAddress = tv_address.getText().toString();
        if (TextUtils.isEmpty(mAddress)) {
            Global.ToastLong("无效地址!请刷新地址后重试");
            return;
        }
        if (isPicture && !(lstData_Attachment.size() > 0)) {
            Global.ToastLong("需要上传照片，请拍照");
            return;
        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("gpsInfo", loPosition + "," + laPosition);//当前定位信息
//        map.put("address", mAddress.trim());//客户地址
        map.put("position", tv_address.getText().toString());//当前定位地址
        map.put("attachmentUUId", uuid);
        map.put("customerId", customerId);
        map.put("audioInfo", audioInfo);
        map.put("atDepts", atDepts);
        map.put("atUserIds", atUserIds);
        map.put("contactName", tv_contact_name.getText().toString());
        if (isCusPosition)
            map.put("isCusPosition", customerAddress);

        if (!StringUtil.isEmpty(edt_memo.getText().toString())) {
            map.put("memo", edt_memo.getText().toString());
        }
        LogUtil.d(" 新增拜访传递数据：" + MainApp.gson.toJson(map));
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSignIn(map, new RCallback<LegWork>() {
            @Override
            public void success(final LegWork legWork, final Response response) {
                HttpErrorCheck.checkResponse(" 新增拜访传result：", response);
                if (legWork != null) {
                    if (!TextUtils.isEmpty(legWork.getId())) {
                        Toast(getString(R.string.sign) + getString(R.string.app_succeed));
                        AppBus.getInstance().post(new SigninNewRushEvent());
                        finish();
                    }
                } else {
                    Toast("提交失败" + response.getStatus());
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
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
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                lstData_Attachment = attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                Toast("获取附件失败");
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (null == data) {
            return;
        }

        /*地图微调，数据回调*/
/*        if (resultCode == MapModifyView.SERACH_MAP) {
            positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
            laPosition = positionResultItem.laPosition;
            loPosition = positionResultItem.loPosition;
            tv_address.setText(positionResultItem.address);
        }*/

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

            case MainApp.GET_IMG:
                try {
                    ArrayList<ImageInfo> pickPhots = (ArrayList<ImageInfo>) data.getSerializableExtra("data");
                    for (ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = null;
                        newFile = Global.scal(this, uri);

                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                /**上传附件*/
                                Utils.uploadAttachment(uuid, 0, newFile).subscribe(new CommonSubscriber(this) {
                                    @Override
                                    public void onNext(final Serializable serializable) {
                                        getAttachments();
                                        pcitureNumber++;
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    LogUtil.dll("IO异常");
                    e.printStackTrace();
                }

                break;
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                try {
                    final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("bizType", 0);
                    map.put("uuid", uuid);
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
                        @Override
                        public void success(final Attachment attachment, final Response response) {
                            Toast("删除附件成功!");
                            lstData_Attachment.remove(delAttachment);
                            init_gridView_photo();
                            pcitureNumber--;
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            Toast("删除附件失败!");
                            super.failure(error);
                        }
                    });
                } catch (Exception e) {
                    Global.ProcException(e);
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
                    getDefaultContact(contactList);
                } else {
                    ll_contact.setVisibility(View.GONE);
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
//                    contactId = contact.getId();
//                    contactName = contact.getName();
                    tv_contact_name.setText(contact.getName());
                }
                break;
        }
    }

    private void distanceInfo(Location loc) {

        if (loc != null && loc.loc != null && loc.loc.size() > 0 && loc.loc.get(0) > 0) {
            tv_distance_deviation.setText(getDeviationDistance(loc.loc.get(0), loc.loc.get(1)));
            tv_distance_deviation.setTextColor(Color.parseColor("#666666"));
        } else {
            tv_distance_deviation.setText("未知");
            tv_distance_deviation.setTextColor(Color.parseColor("#f5625a"));
        }
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
//                contactId = ele.getId();
//                contactName = ele.getName();
                tv_contact_name.setText(ele.getName());
            }
        }
    }

    private String getDeviationDistance(double la, double lo) {
        LatLng ll = new LatLng(laPosition, loPosition);
        LatLng llCustomer = new LatLng(lo, la);// 地点的纬度，在-90 与90 之间的double 型数值。、地点的经度，在-180 与180 之间的double 型数值。
        LogUtil.d("偏差距离:" + AMapUtils.calculateLineDistance(ll, llCustomer));
        Double distance = Double.valueOf(Utils.setValueDouble2(AMapUtils.calculateLineDistance(ll, llCustomer)));
        DecimalFormat df = new DecimalFormat("0.00");
        String distanceText;
        if (distance <= 1000) {
            distanceText = Utils.setValueDouble2(distance) + "m";
        } else {
            distanceText = df.format(distance / 1000) + "km";
        }

        return distanceText;
    }//  104.073255,30.689493


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
}
