package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.CustomerExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraProperties;
import com.loyo.oa.v2.activityui.customer.model.HttpAddCustomer;
import com.loyo.oa.v2.activityui.customer.model.Locate;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.customview.CustomerInfoExtraData;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * 【新建 客户】 页面
 */
@EActivity(R.layout.activity_customer_add)
public class CustomerAddActivity extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_NEW_CONTRACT = 6;
    public static final int REQUEST_CUSTOMER_SERACH = 7;

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    EditText edt_name;
    @ViewById
    EditText edt_contract;
    @ViewById
    EditText et_address;
    @ViewById
    TextView tv_labels;
    @ViewById
    LinearLayout layout_newContract;
    @ViewById
    LinearLayout layout_address;
    @ViewById
    Button btn_add_new_contract;
    @ViewById
    CusGridView gridView_photo;
    @ViewById
    TextView tv_gscx;
    //新建拜访 过来新建客户成功过后需要把数据回传到新建拜访页面
    @Extra("isResultSignin")
    boolean isResultSignin = false;


    private TextView tv_phone_name1;
    private TextView tv_phone_name2;
    private TextView tv_phone_name3;

    private TextView tv_call_name1;
    private TextView tv_call_name2;
    private TextView tv_call_name3;

    private EditText edt_contract_tel1;
    private EditText edt_contract_tel2;
    private EditText edt_contract_tel3;

    private EditText edt_contract_telnum1;
    private EditText edt_contract_telnum2;
    private EditText edt_contract_telnum3;

    private ImageView iv_phone_insert1;
    private ImageView iv_phone_insert2;
    private ImageView iv_phone_insert3;

    private ImageView iv_call_insert1;
    private ImageView iv_call_insert2;
    private ImageView iv_call_insert3;

    private LinearLayout ll_phone_layout2;
    private LinearLayout ll_phone_layout3;
    private LinearLayout ll_call_layout2;
    private LinearLayout ll_call_layout3;
    private LinearLayout layout_more;
    private LinearLayout container;

    private EditText edit_address_details;
    private ImageGridViewAdapter imageGridViewAdapter;
    private ArrayList<Contact> mContacts = new ArrayList<>();
    private ArrayList<NewTag> tags;
    private Bundle mBundle;
    private List<String> mSelectPath;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();

    private String uuid;
    private String tagItemIds;
    private String myAddress;
    private String contactName = "";
    private String contactPhone = "";

    private String customer_name;
    private String customerAddress;
    private String customerContract;
    private String customerContractTel;
    private String customerWrietele;
    private String cusotmerDetalisAddress;

    private int bizType = 0x01;
    private int uploadSize;
    private int uploadNum;

    private double laPosition;//当前位置的经纬度
    private double loPosition;

    private boolean cusGuys = false;  //联系人权限
    private boolean cusPhone = false; //手机权限
    private boolean cusMobile = false;//座机权限
    private boolean cusLocation = false;//定位权限
    private boolean cusDetialAdress = false;//客户的详细地址
    private boolean isSave = true;
    private Customer mCustomer;
    private ArrayList<String> telGroup;
    private ArrayList<String> wiretelGroup;

    private ArrayList<ContactLeftExtras> mCustomerExtraDatas;
    private PositionResultItem positionResultItem;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(final Message msg) {
            if (msg.what == 0x01) {
                et_address.setText(myAddress);
                edit_address_details.setText(myAddress);
            }
        }
    };

    @AfterViews
    void initUI() {
        contactName = getIntent().getStringExtra(ExtraAndResult.EXTRA_NAME);
        contactPhone = getIntent().getStringExtra(ExtraAndResult.EXTRA_DATA);

        tv_phone_name1 = (TextView) findViewById(R.id.tv_phone_name1);
        tv_phone_name2 = (TextView) findViewById(R.id.tv_phone_name2);
        tv_phone_name3 = (TextView) findViewById(R.id.tv_phone_name3);

        tv_call_name1 = (TextView) findViewById(R.id.tv_call_name1);
        tv_call_name2 = (TextView) findViewById(R.id.tv_call_name2);
        tv_call_name3 = (TextView) findViewById(R.id.tv_call_name3);

        edt_contract_tel1 = (EditText) findViewById(R.id.edt_contract_tel1);
        edt_contract_tel2 = (EditText) findViewById(R.id.edt_contract_tel2);
        edt_contract_tel3 = (EditText) findViewById(R.id.edt_contract_tel3);

        edt_contract_telnum1 = (EditText) findViewById(R.id.edt_contract_telnum1);
        edt_contract_telnum2 = (EditText) findViewById(R.id.edt_contract_telnum2);
        edt_contract_telnum3 = (EditText) findViewById(R.id.edt_contract_telnum3);

        iv_phone_insert1 = (ImageView) findViewById(R.id.iv_phone_insert1);
        iv_phone_insert2 = (ImageView) findViewById(R.id.iv_phone_insert2);

        iv_call_insert1 = (ImageView) findViewById(R.id.iv_call_insert1);
        iv_call_insert2 = (ImageView) findViewById(R.id.iv_call_insert2);

        ll_phone_layout2 = (LinearLayout) findViewById(R.id.ll_phone_layout2);
        ll_phone_layout3 = (LinearLayout) findViewById(R.id.ll_phone_layout3);
        ll_call_layout2 = (LinearLayout) findViewById(R.id.ll_call_layout2);
        ll_call_layout3 = (LinearLayout) findViewById(R.id.ll_call_layout3);
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        container = (LinearLayout) findViewById(R.id.layout_customer_extra_info);

        layout_more.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        iv_phone_insert1.setOnTouchListener(Global.GetTouch());
        iv_phone_insert2.setOnTouchListener(Global.GetTouch());
        iv_call_insert1.setOnTouchListener(Global.GetTouch());
        iv_call_insert2.setOnTouchListener(Global.GetTouch());

        edit_address_details = (EditText) findViewById(R.id.edit_address_details);
        super.setTitle("新建客户");
        init_gridView_photo();
        getTempCustomer();
        startLocation();
        requestJurisdiction();
        if (app.latitude != -1 && app.longitude != -1) {
            laPosition = app.latitude;
            loPosition = app.longitude;
        }
        edt_contract_tel1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edt_contract_telnum1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        if (!TextUtils.isEmpty(contactName)) {
            edt_contract.setText(contactName);
        }
        if (!TextUtils.isEmpty(contactPhone)) {
            edt_contract_tel1.setText(contactPhone.replaceAll(" ", ""));
        }
    }

    LocationUtilGD locationGd;

    /**
     * 获取定位
     */
    void startLocation() {

        et_address.setText(app.address);
        locationGd = new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                UMengTools.sendLocationInfo(address, longitude, latitude);
                myAddress = address;
                mHandler.sendEmptyMessage(0x01);
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                Toast("定位失败,请在网络和GPS信号良好时重试");
                LocationUtilGD.sotpLocation();
            }
        });
    }


    /**
     * 组装动态数据
     */
    private void initExtra(final boolean ismy) {
        if (null == mCustomerExtraDatas || mCustomerExtraDatas.size() == 0) {
            layout_more.setVisibility(View.GONE);
            return;
        }
        ArrayList<ExtraData> extDatas = new ArrayList<>();
        ExtraData extraData;
        ExtraProperties properties;

        for (ContactLeftExtras contactLeftExtras : mCustomerExtraDatas) {
            extraData = new ExtraData();
            properties = new ExtraProperties();
            if (!contactLeftExtras.isSystem && contactLeftExtras.enabled) {
                properties.setEnabled(contactLeftExtras.enabled);
                properties.setRequired(contactLeftExtras.required);
                properties.setLabel(contactLeftExtras.label);
                properties.setType(contactLeftExtras.type);
                properties.setIsList(contactLeftExtras.isList);
                properties.setDefVal(contactLeftExtras.defVal);
                extraData.setProperties(properties);
            }
            extDatas.add(extraData);
        }
        container.addView(new CustomerInfoExtraData(mContext, extDatas, ismy, R.color.title_bg1, 0, true, true, false));
    }

    void getTempCustomer() {
        Customer customer = DBManager.Instance().getCustomer();
        if (customer == null) return;

        edt_name.setText(customer.name);
        ArrayList<Contact> contacts = customer.contacts;
        if (contacts != null && contacts.size() > 0) {
            for (Contact c : contacts) {
                if (c.isDefault()) {
                    edt_contract.setText(c.getName());
                    edt_contract_tel1.setText(c.getTel());
                } else {
                    setContract(c);
                }
            }
        }
    }


    /**
     * 批量上传附件
     */
    private void newUploadAttachement(final Customer customer) {
        showLoading("正在提交附件");
        try {
            uploadSize = 0;
            uploadNum = pickPhots.size();
            for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        TypedFile typedFile = new TypedFile("image/*", newFile);
                        TypedString typedUuid = new TypedString(uuid);
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                                new RCallback<Attachment>() {
                                    @Override
                                    public void success(final Attachment attachments, final Response response) {
                                        uploadSize++;
                                        if (uploadSize == uploadNum) {
                                            cancelLoading();
                                            customerSendSucess(customer);
                                        }
                                    }

                                    @Override
                                    public void failure(final RetrofitError error) {
                                        super.failure(error);
                                        HttpErrorCheck.checkError(error);
                                    }
                                });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }


    void init_gridView_photo() {
        imageGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, imageGridViewAdapter);
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.tv_search,
            R.id.layout_customer_label, R.id.img_refresh_address, R.id.iv_phone_insert1,
            R.id.iv_phone_insert2, R.id.iv_call_insert1, R.id.iv_call_insert2, R.id.tv_gscx, R.id.layout_more})
    public void onClick(final View v) {
        switch (v.getId()) {

            /*更多信息*/
            case R.id.layout_more:
                layout_more.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                break;

            /*手机添加1*/
            case R.id.iv_phone_insert1:
                tv_phone_name1.setText("手机1    ");
                tv_phone_name2.setText("手机2    ");
                iv_phone_insert1.setVisibility(View.INVISIBLE);
                ll_phone_layout2.setVisibility(View.VISIBLE);
                break;

            /*手机添加2*/
            case R.id.iv_phone_insert2:
                tv_phone_name3.setText("手机3    ");
                iv_phone_insert2.setVisibility(View.INVISIBLE);
                ll_phone_layout3.setVisibility(View.VISIBLE);
                break;

            /*电话添加1*/
            case R.id.iv_call_insert1:
                tv_call_name1.setText("座机1    ");
                tv_call_name2.setText("座机2    ");
                iv_call_insert1.setVisibility(View.INVISIBLE);
                ll_call_layout2.setVisibility(View.VISIBLE);
                break;

            /*电话添加2*/
            case R.id.iv_call_insert2:
                tv_call_name3.setText("座机3    ");
                iv_call_insert2.setVisibility(View.INVISIBLE);
                ll_call_layout3.setVisibility(View.VISIBLE);
                break;

            /*刷新地址*/
            case R.id.img_refresh_address:

                mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);

                break;

            /*查重*/
            case R.id.tv_search:
                if (!edt_name.getText().toString().isEmpty()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("name", edt_name.getText().toString());
                    app.startActivityForResult((Activity) mContext, CustomerRepeat.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_SERACH, bundle1);

                } else {
                    Toast("客户名称不能为空");
                }
                break;
             /*工商查询*/
            case R.id.tv_gscx:
                if (!edt_name.getText().toString().isEmpty()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("Keyword", edt_name.getText().toString());
                    app.startActivityForResult((Activity) mContext, BusinessInquiryActivity.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_SERACH, bundle1);

                } else {
                    Toast("客户名称不能为空");
                }
                break;

            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            /*提交*/
            case R.id.img_title_right:

                uuid = StringUtil.getUUID();
                customer_name = edt_name.getText().toString().trim();
                customerAddress = et_address.getText().toString().trim();
                customerContract = edt_contract.getText().toString().trim();
                customerContractTel = edt_contract_tel1.getText().toString().trim();
                customerWrietele = edt_contract_telnum1.getText().toString().trim();
                cusotmerDetalisAddress = edit_address_details.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    Toast("请输入客户名称!");
                    return;
                } else if (customerAddress.isEmpty() && cusLocation) {
                    Toast("请输入的客户地址!");
                    return;
                } else if (cusotmerDetalisAddress.isEmpty() && cusDetialAdress) {
                    Toast("请输入的客户详细地址!");
                    return;
                } else if (TextUtils.isEmpty(customerContractTel) && cusPhone) {
                    Toast("请输入客户手机号码!");
                    return;
                } else if (TextUtils.isEmpty(customerWrietele) && cusMobile) {
                    Toast("请输入客户座机号码!");
                    return;
                } else if (TextUtils.isEmpty(customerContract) && cusGuys) {
                    Toast("请输入联系人姓名!");
                    return;
                }

/*                if(!customerContractTel.isEmpty()){
                    if(!RegularCheck.isMobilePhone(customerContractTel)){
                        Toast("手机号码格式不正确");
                        return;
                    }
                }

                if(!customerWrietele.isEmpty()){
                    if(!RegularCheck.isPhone(customerWrietele)){
                        Toast("座机号码格式不正确");
                        return;
                    }
                }*/
                showLoading("");
                requestCommitTask();
                break;

            /*选择标签*/
            case R.id.layout_customer_label:
                Bundle bundle2 = new Bundle();
                if (tags != null) {
                    bundle2.putSerializable("tags", tags);
                }
                app.startActivityForResult((Activity) mContext, CustomerLabelActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_LABEL, bundle2);

                break;

            default:
                break;
        }
    }


    void setContract(final Contact c) {
        if (c == null) return;
        mContacts.add(c);
        layout_newContract.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.item_customer_new_contract, null, false);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_name.setText(c.getName());
        tv_phone.setText(c.getTel());
        layout_newContract.addView(view);
    }

    /**
     * 获取新建客户权限
     */
    public void requestJurisdiction() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 100);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getAddCustomerJur(map, new RCallback<ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(final ArrayList<ContactLeftExtras> cuslist, final Response response) {
                HttpErrorCheck.checkResponse("获取新建客户权限", response);
                mCustomerExtraDatas = cuslist;
                for (ContactLeftExtras customerJur : cuslist) {
                    if (customerJur.label.contains("联系人") && customerJur.required) {
                        cusGuys = true;
                        edt_contract.setHint("请输入联系人姓名(必填)");
                    } else if (customerJur.label.contains("手机") && customerJur.required) {
                        cusPhone = true;
                        edt_contract_tel1.setHint("限数字,如13912345678(必填)");
                    } else if (customerJur.label.contains("座机") && customerJur.required) {
                        cusMobile = true;
                        edt_contract_telnum1.setHint("限数字,如02812345678(必填)");
                    } else if (customerJur.label.contains("定位") && customerJur.required) {
                        cusLocation = true;//定位必填
                    } else if (customerJur.label.contains("客户地址") && customerJur.required) {
                        cusDetialAdress = true;//详细地址必填
                        edit_address_details.setHint("请输入客户详细地址(必填)");
                    }
                }
                initExtra(true);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    public void getTelNum(EditText editText, ArrayList<String> arrayList) {
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            arrayList.add(editText.getText().toString());
        }
    }


    /**
     * 新建客户请求
     */
    public void requestCommitTask() {
        HttpAddCustomer positionData = new HttpAddCustomer();
        positionData.loc.addr = customerAddress;
        positionData.loc.loc.add(loPosition);
        positionData.loc.loc.add(laPosition);
        if (tags != null && tags.size() > 0) {
            for (NewTag tag : tags) {
                NewTag newtag = new NewTag();
                newtag.tId = tag.tId;
                newtag.itemId = tag.itemId;
                newtag.itemName = tag.itemName;
                positionData.tags.add(newtag);
            }
        }

        HttpAddCustomer locData = new HttpAddCustomer();
        locData.loc.addr = cusotmerDetalisAddress;

        telGroup = new ArrayList<>();
        wiretelGroup = new ArrayList<>();

        getTelNum(edt_contract_tel1, telGroup);
        getTelNum(edt_contract_tel2, telGroup);
        getTelNum(edt_contract_tel3, telGroup);
        getTelNum(edt_contract_telnum1, wiretelGroup);
        getTelNum(edt_contract_telnum2, wiretelGroup);
        getTelNum(edt_contract_telnum3, wiretelGroup);

        HashMap<String, Object> map = new HashMap<>();
        if (pickPhots.size() > 0) {
            map.put("attachmentCount", pickPhots.size());
            map.put("uuid", uuid);
        }

        map.put("position", positionData.loc); //定位数据
        map.put("loc", locData.loc);          //地址详情数据
        map.put("name", customer_name);
        map.put("pname", customerContract);
        /*map.put("ptel", customerContractTel);
        map.put("wiretel", customerWrietele);*/
        map.put("tags", positionData.tags);
        map.put("telGroup", telGroup);
        map.put("wiretelGroup", wiretelGroup);

        LogUtil.dee("新建客户map:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addNewCustomer(map, new RCallback<Customer>() {
            @Override
            public void success(final Customer customer, final Response response) {
                HttpErrorCheck.checkResponse("新建客户", response);
                //没有附件
                if (pickPhots.size() == 0) {
                    customerSendSucess(customer);
                } else {
                    newUploadAttachement(customer);
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
     * 数据提交成功关闭页面
     *
     * @param retCustomer
     */
    public void customerSendSucess(Customer retCustomer) {
        MyCustomerListRushEvent event = new MyCustomerListRushEvent();
        AppBus.getInstance().post(event);
        isSave = false;
        if (isResultSignin) {
            Intent intent = new Intent();
            intent.putExtra("id", retCustomer.id);
            intent.putExtra("name", retCustomer.name);
            Locate oldeLoc = retCustomer.position;
            List<Double> loc = new ArrayList<>();
            loc.add(oldeLoc.loc[0]);
            loc.add(oldeLoc.loc[1]);
            Location location = new Location(loc, oldeLoc.addr);
            intent.putExtra("loc", location);
            setResult(RESULT_OK, intent);
        }
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DBManager.Instance().deleteCustomer();
        if (isSave) {
            mCustomer = new Customer();
            mCustomer.name = (edt_name.getText().toString().trim());

            ArrayList<Contact> contacts = new ArrayList<>();

            if (mContacts != null && mContacts.size() > 0) {
                contacts.addAll(mContacts);
            }

            Contact defaultContact = new Contact();
            defaultContact.setName(edt_contract.getText().toString());
            defaultContact.setTel(edt_contract_tel1.getText().toString());
            defaultContact.setIsDefault(true);
            contacts.add(0, defaultContact);
            mCustomer.contacts = contacts;

            mCustomer.owner = null;
            mCustomer.members = null;
            mCustomer.tags = null;
            mCustomer.creator = null;

            DBManager.Instance().putCustomer(MainApp.gson.toJson(mCustomer));
        }

        locationGd.sotpLocation();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        switch (requestCode) {
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    et_address.setText(positionResultItem.address);
                    edit_address_details.setText(positionResultItem.address);
                }
                break;

            case REQUEST_CUSTOMER_SERACH:

                Bundle bundle1 = data.getExtras();
                edt_name.setText(bundle1.getString("name"));

                break;

            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                tags = (ArrayList<NewTag>) bundle.getSerializable("data");
                StringBuffer sb = null;
                StringBuffer sbItemId = null;
                StringBuffer sbTId = null;
                for (NewTag tag : tags) {
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId = new StringBuffer();
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId = new StringBuffer();
                        sbTId.append(String.valueOf(tag.gettId()));

                    } else {
                        sb.append("、");
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId.append(",");
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId.append(",");
                        sbTId.append(String.valueOf(tag.gettId()));
                    }
                }

                if (sbItemId != null) {
                    tagItemIds = sbItemId.toString();
                    tv_labels.setText(sb.toString());
                } else
                    tv_labels.setText("");
                break;

            case REQUEST_CUSTOMER_NEW_CONTRACT:

                Contact contact = (Contact) data.getSerializableExtra("data");
                setContract(contact);

                break;

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    pickPhotsResult = new ArrayList<>();
                    mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        pickPhotsResult.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
                    }
                    pickPhots.addAll(pickPhotsResult);
                    init_gridView_photo();
                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                init_gridView_photo();
                break;

            default:
                break;
        }
    }
}