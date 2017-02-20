package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.type.LoyoBizType;
import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.clue.model.ClueSales;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerRegional;
import com.loyo.oa.v2.activityui.customer.model.CustomerStatusModel;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.ExtraProperties;
import com.loyo.oa.v2.activityui.customer.model.HttpLoc;
import com.loyo.oa.v2.activityui.customer.model.Locate;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.model.CustomerWrapper;
import com.loyo.oa.v2.customview.CustomerInfoExtraData;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.ids.gws.android.sclick.SClick;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 【新建 客户】 页面
 */

@EActivity(R.layout.activity_customer_add)
public class CustomerAddActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback {

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_NEW_CONTRACT = 6;
    public static final int REQUEST_CUSTOMER_SERACH = 7;
    private static final int REQUEST_ACTIVITY_CODE_STATUS = 8;//resule code
    public static final int TYPE_CLUE_TO_CUSTOMER = 1223;//线索转为客户
    public static final int TYPE_NEW_CUSTOMER_FROM_CONTACT = 1224;//新建客户，但是来自通讯录导入

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
    @ViewById(R.id.image_upload_grid_view)
    ImageUploadGridView gridView;
    @ViewById
    TextView tv_gscx;
    //新建拜访 过来新建客户成功过后需要把数据回传到新建拜访页面
    @Extra("isResultSignin")
    boolean isResultSignin = false;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    int actionType;
    @Extra(ExtraAndResult.EXTRA_DATA)
    ClueSales clueSales;//线索转为客户的数据


    @ViewById
    LinearLayout layout_customer_status, layout_customer_label;

    @ViewById
    TextView tv_status;//客户状态
    @ViewById
    EditText edt_customer_weburl;

    //手机
    @ViewById
    TextView tv_phone_name1, tv_phone_name2, tv_phone_name3;

    //座机
    @ViewById
    TextView tv_call_name1, tv_call_name2, tv_call_name3;

    @ViewById
    TextView tv_district;
    @ViewById
    EditText edt_contract_tel1, edt_contract_tel2, edt_contract_tel3, edt_contract_telnum1, edt_contract_telnum2, edt_contract_telnum3, edt_content;

    @ViewById
    ImageView iv_phone_insert1, iv_phone_insert2, iv_call_insert1, iv_call_insert2;

    @ViewById
    LinearLayout ll_phone_layout2, ll_phone_layout3, ll_call_layout2, ll_call_layout3, layout_more, layout_customer_optional_info, layout_customer_required_info;

    private EditText edit_address_details;
    private ArrayList<Contact> mContacts = new ArrayList<>();
    private Bundle mBundle;
    private ArrayList<ExtraData> RextDatasModel;  //必填
    private ArrayList<ExtraData> OpextDatasModel; //选填

    private String uuid;

    private boolean cusMemo = false; //简介权限
    private boolean cusGuys = false;  //联系人权限
    private boolean cusPhone = false; //手机权限
    private boolean cusMobile = false;//座机权限
    private boolean cusLocation = false;//定位权限
    private boolean cusDetialAdress = false;//客户的详细地址
    private boolean isSave = true;

    private ArrayList<ContactLeftExtras> mCustomerExtraDatas;
    private PositionResultItem positionResultItem;

    UploadController controller;
    LocationUtilGD locationGd;
    private CompositeSubscription subscriptions;

    private Customer customer = new Customer();//新建的客户

    @AfterViews
    void initUI() {
        subscriptions = new CompositeSubscription();
        Global.SetTouchView(layout_more, img_title_left, img_title_right, iv_phone_insert1, iv_phone_insert2,
                iv_call_insert1, iv_call_insert2, layout_customer_status, layout_customer_label);
        edit_address_details = (EditText) findViewById(R.id.edit_address_details);

        edt_contract_tel1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        edt_contract_telnum1.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        LocationUtilGD.permissionLocation(this);
        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);
        /*线索转为客户*/
        if (actionType == TYPE_CLUE_TO_CUSTOMER && clueSales != null) {
            super.setTitle("线索转换客户");
            edt_name.setText(clueSales.companyName);
            edit_address_details.setText(clueSales.address);
            edt_contract.setText(clueSales.name);
            edt_contract_tel1.setText(clueSales.cellphone);
            edt_contract_telnum1.setText(clueSales.tel);
        } else if (actionType == TYPE_NEW_CUSTOMER_FROM_CONTACT && clueSales != null) {
            //来自通讯录导入
            super.setTitle("新建客户");
            if (null != clueSales.name) {
                edt_contract.setText(clueSales.name);
            }
            if (null != clueSales.tel) {
                edt_contract_tel1.setText(clueSales.tel.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", ""));
            }
        } else {
            super.setTitle("新建客户");
            getTempCustomer();
        }
        requestJurisdiction();
        startLocation();
    }

    /**
     * 获取定位
     */
    void startLocation() {
        locationGd = new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                UMengTools.sendLocationInfo(address, longitude, latitude);
                et_address.setText(address);
                if (TextUtils.isEmpty(edit_address_details.getText().toString())) {
                    edit_address_details.setText(address);
                }
                app.address = address;//缓存最新的地址
                app.longitude = longitude;
                app.latitude = latitude;
                Locate postition = new Locate();
                customer.position = postition;
                customer.position.addr = address;
                customer.position.loc[0] = longitude;
                customer.position.loc[1] = latitude;
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                Toast(R.string.LOCATION_FAILED);
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
        RextDatasModel = new ArrayList<>();
        OpextDatasModel = new ArrayList<>();
        ExtraData extraData;
        ExtraProperties properties;

        //说明没有保存的有缓存
        if (customer.extDatas.size() <= 0) {
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
                    properties.setName(contactLeftExtras.name);
                    extraData.setProperties(properties);
                    customer.extDatas.add(extraData);
                }
            }
        }


        /*分离必填与非必填字段*/
        for (ExtraData ext : customer.extDatas) {
            if (ext.getProperties().isRequired()) {
                RextDatasModel.add(ext);
            } else {
                OpextDatasModel.add(ext);
                //显示更多信息
                if (!TextUtils.isEmpty(ext.getVal())) {
                    layout_more.setVisibility(View.GONE);
                    layout_customer_optional_info.setVisibility(View.VISIBLE);
                }
            }
        }

        layout_customer_optional_info.addView(new CustomerInfoExtraData(mContext, OpextDatasModel, ismy, R.color.text33, 0));
        layout_customer_required_info.addView(new CustomerInfoExtraData(mContext, RextDatasModel, ismy, R.color.text33, 0));
    }

    /**
     * 加载上次编辑，没有提交的信息
     */
    void getTempCustomer() {
        customer = DBManager.Instance().getCustomer();
        if (customer == null){
            customer = new Customer();
        }
        Toast("已显示之前未提交的数据");
        edt_name.setText(customer.name);
        if (null != customer.position) edit_address_details.setText(customer.position.addr);
        if (null != customer.loc) et_address.setText(customer.loc.addr);
        if (!TextUtils.isEmpty(customer.statusName)) tv_status.setText(customer.statusName);
        if (null != customer.tags) tv_labels.setText(customer.displayTagString());
        if (!TextUtils.isEmpty(customer.webSite)) edt_customer_weburl.setText(customer.webSite);
        if (!TextUtils.isEmpty(customer.summary)) edt_content.setText(customer.summary);
        if (null != customer.regional) {
            tv_district.setText(customer.regional.province + " " + customer.regional.city + " " + customer.regional.county);
            layout_more.setVisibility(View.GONE);
            layout_customer_optional_info.setVisibility(View.VISIBLE);
        }
        //恢复联系人和电话号码
        if (null != customer.contacts && customer.contacts.size() > 0) {
            Contact contact = customer.contacts.get(0);
            edt_contract.setText(contact.getName());
            for (int i = 0; i < contact.telGroup.size(); i++) {
                switch (i) {
                    case 0:
                        edt_contract_tel1.setText(contact.telGroup.get(i));
                        break;
                    case 1:
                        edt_contract_tel2.setText(contact.telGroup.get(i));
                        tv_phone_name1.setText("手机1    ");
                        tv_phone_name2.setText("手机2    ");
                        iv_phone_insert1.setVisibility(View.INVISIBLE);
                        ll_phone_layout2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        edt_contract_tel3.setText(contact.telGroup.get(i));
                        tv_phone_name3.setText("手机3    ");
                        iv_phone_insert2.setVisibility(View.INVISIBLE);
                        ll_phone_layout3.setVisibility(View.VISIBLE);
                        break;
                }
            }
            for (int i = 0; i < contact.wiretelGroup.size(); i++) {
                switch (i) {
                    case 0:
                        edt_contract_telnum1.setText(contact.wiretelGroup.get(i));
                        break;
                    case 1:
                        edt_contract_telnum2.setText(contact.wiretelGroup.get(i));
                        tv_call_name1.setText("座机1    ");
                        tv_call_name2.setText("座机2    ");
                        iv_call_insert1.setVisibility(View.INVISIBLE);
                        ll_call_layout2.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        edt_contract_telnum3.setText(contact.wiretelGroup.get(i));
                        tv_call_name3.setText("座机3    ");
                        iv_call_insert2.setVisibility(View.INVISIBLE);
                        ll_call_layout3.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.tv_search,
            R.id.layout_customer_label, R.id.img_refresh_address, R.id.iv_phone_insert1,
            R.id.iv_phone_insert2, R.id.iv_call_insert1, R.id.iv_call_insert2, R.id.tv_gscx, R.id.layout_more,
            R.id.layout_customer_district, R.id.layout_customer_status})
    public void onClick(final View v) {
        switch (v.getId()) {

            /*更多信息*/
            case R.id.layout_more:
                layout_more.setVisibility(View.GONE);
                layout_customer_optional_info.setVisibility(View.VISIBLE);
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

            /*地区*/
            case R.id.layout_customer_district:
                loadAreaCodeTable();
                break;


            /*刷新地址*/
            case R.id.img_refresh_address:
                if (LocationUtilGD.permissionLocation(CustomerAddActivity.this)) {
                    mBundle = new Bundle();
                    mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                    app.startActivityForResult(CustomerAddActivity.this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                }
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
                    //统计工商查询次数(正式环境)
                    UmengAnalytics.umengSend(mContext, UmengAnalytics.businessQuery);
                } else {
                    Toast("客户名称不能为空");
                }
                break;

            case R.id.img_title_left:
                app.finishActivity(CustomerAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            /*提交*/
            case R.id.img_title_right:

                if (!Utils.isNetworkAvailable(mContext)) {
                    Toast("请检查您的网络连接");
                    return;
                }

                uuid = StringUtil.getUUID();
                customer.name = edt_name.getText().toString().trim();
                Locate loc = new Locate();
                customer.loc = loc;
                customer.loc.addr = edit_address_details.getText().toString().trim();
                customer.summary = edt_content.getText().toString().trim();
                //联系人
                Contact firstContact = new Contact();
                firstContact.setName(edt_contract.getText().toString().trim());
                firstContact.setTel(edt_contract_telnum1.getText().toString().trim());
                firstContact.setWiretel(edt_contract_telnum1.getText().toString().trim());
                customer.contacts.add(firstContact);
                if (customer.name.isEmpty()) {
                    Toast("请输入客户名称!");
                    return;
                } else if (null != customer.position && TextUtils.isEmpty(customer.position.addr) && cusLocation) {
                    Toast("请输入的客户地址!");
                    return;
                } else if (null != customer.loc && TextUtils.isEmpty(customer.loc.addr) && cusDetialAdress) {
                    Toast("请输入的客户详细地址!");
                    return;
                } else if (TextUtils.isEmpty(firstContact.getTel()) && cusPhone) {
                    Toast("请输入客户手机号码!");
                    return;
                } else if (TextUtils.isEmpty(firstContact.getWiretel()) && cusMobile) {
                    Toast("请输入客户座机号码!");
                    return;
                } else if (TextUtils.isEmpty(firstContact.getName()) && cusGuys) {
                    Toast("请输入联系人姓名!");
                    return;
                } else if (TextUtils.isEmpty(customer.summary) && cusMemo) {
                    Toast("请填写客户简介!");
                    return;
                } else if (!testDynamicword()) {
                    Toast("请填写必填字段!");
                    return;
                } else if (!SClick.check(SClick.BUTTON_CLICK, 5000)) {
                    return;
                }

                showCommitLoading();
                if (controller.count() <= 0) {
                    requestCommitTask();
                } else {
                    controller.startUpload();
                    controller.notifyCompletionIfNeeded();
                }
                break;

            /*选择标签*/
            case R.id.layout_customer_label:
                Intent mIntent = new Intent(this, CustomerLabelCopyActivity.class);
                mIntent.putExtra("canEdit", true);
                mIntent.putExtra("fromPage", 1);
                mIntent.putExtra("customerId", "-1");
                if (null != customer.tags) {
                    mIntent.putExtra("tagitems", Utils.convertTagItems(customer.tags));
                }
                startActivity(mIntent);
                break;
            /*选择状态*/
            case R.id.layout_customer_status:
                Bundle b = new Bundle();
                b.putString(CustomerStatusSingleSelectActivity.EXTRA_CURRENT, customer.statusId);//设置默认值
                app.startActivityForResult(this, CustomerStatusSingleSelectActivity.class, app.ENTER_TYPE_RIGHT, REQUEST_ACTIVITY_CODE_STATUS, b);
                break;
        }
    }

    /**
     * 更新Label
     *
     * @param event
     */
    @Subscribe
    public void onCustomerRushEvent(MyCustomerRushEvent event) {
        if (MyCustomerRushEvent.EVENT_CODE_UPDATE == event.eventCode &&
                event.subCode == MyCustomerRushEvent.EVENT_SUB_CODE_LABEL
                && "-1".equals(event.session)) {
            customer.tags = event.data.tags;
            tv_labels.setText(customer.displayTagString());

        }
    }

    /**
     * 显示地区选择Dialog
     */
    void loadAreaCodeTable() {
        String[] cityValue = null;
        if (!tv_district.getText().toString().isEmpty()) {
            cityValue = tv_district.getText().toString().split(" ");
        }
        final SelectCityView selectCityView = new SelectCityView(this, cityValue);
        selectCityView.setCanceledOnTouchOutside(true);
        selectCityView.show();
        selectCityView.setOnclickselectCity(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] cityArr = selectCityView.getResult();
                tv_district.setText(cityArr[0] + " " + cityArr[1] + " " + cityArr[2]);
                customer.regional = new CustomerRegional();
                customer.regional.province = cityArr[0];
                customer.regional.city = cityArr[1];
                customer.regional.county = cityArr[2];
                selectCityView.dismiss();
            }
        });
    }

    /**
     * 获取新建客户权限
     */
    public void requestJurisdiction() {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 100);
        CustomerService.getAddCustomerJur(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>(hud) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LoyoToast.error(CustomerAddActivity.this, "获取数据失败，请重试");
                        finish();
                    }

                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        mCustomerExtraDatas = contactLeftExtrasArrayList;
                        for (ContactLeftExtras customerJur : contactLeftExtrasArrayList) {
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
                            } else if (customerJur.label.contains("简介") && customerJur.required) {
                                cusMemo = true;
                                edt_content.setHint("客户简介(必填)");
                            }
                        }
                        initExtra(true);

                    }
                });
    }

    public void getTelNum(EditText editText, ArrayList<String> arrayList) {
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            arrayList.add(editText.getText().toString());
        }
    }

    /**
     * 验证必填动态字段是否填写
     */

    private boolean testDynamicword() {
        customer.extDatas.clear();
        customer.extDatas.addAll(RextDatasModel);
        customer.extDatas.addAll(OpextDatasModel);
        for (ExtraData ext : customer.extDatas) {
            try {
                if (ext.getProperties().isRequired() && ext.getProperties().isEnabled()) {
                    if (ext.getVal().isEmpty() || null == ext.getVal()) {
                        return false;
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     * 新建客户请求
     */
    public void requestCommitTask() {
        //获取联系人号码
        Contact contact = customer.contacts.get(0);
        contact.telGroup.clear();//清除验证数据的时候的输入
        contact.wiretelGroup.clear();//清除验证数据的时候的输入
        getTelNum(edt_contract_tel1, contact.telGroup);
        getTelNum(edt_contract_tel2, contact.telGroup);
        getTelNum(edt_contract_tel3, contact.telGroup);
        getTelNum(edt_contract_telnum1, contact.wiretelGroup);
        getTelNum(edt_contract_telnum2, contact.wiretelGroup);
        getTelNum(edt_contract_telnum3, contact.wiretelGroup);

        HashMap<String, Object> map = new HashMap<>();
        if (controller.count() > 0) {
            map.put("attachmentCount", controller.count());
            map.put("uuid", uuid);
        }
        //因为数据格式的原因，把保存在customer的数据，转换一个格式然后再提交
        HttpLoc position = new HttpLoc();
        position.addr = customer.position.addr;
        position.loc.add(customer.position.loc[0]);
        position.loc.add(customer.position.loc[1]);

        map.put("position", position); //定位数据
        map.put("loc", customer.loc);//地址详情数据
        map.put("name", customer.name);
        map.put("pname", contact.getName());
        map.put("tags", customer.tags);
        map.put("telGroup", contact.telGroup);
        map.put("wiretelGroup", contact.wiretelGroup);
        map.put("extDatas", customer.extDatas);
        map.put("summary", customer.summary);
        map.put("regional", customer.regional);
        if (!TextUtils.isEmpty(customer.statusId)) {
            map.put("statusId", customer.statusId);//选中的有statusId，就提交不然，服务器给来默认值的
        }
        customer.webSite = edt_customer_weburl.getText() + "";
        map.put("webSite", customer.webSite);
        if (actionType == TYPE_CLUE_TO_CUSTOMER && clueSales != null)
            map.put("salesleadId", clueSales.id);
        Subscription subscribe = CustomerService.addNewCustomer(map)
                .subscribe(new DefaultLoyoSubscriber<CustomerWrapper>(hud) {
                    @Override
                    public void onNext(final CustomerWrapper customer) {
                        if (customer.errcode != 0) {
                            //删除本地缓存
                            DBManager.Instance().deleteCustomer();
                            hud.dismiss();
                            showCommitLoading();
                            LoyoUIThread.runAfterDelay(new Runnable() {
                                @Override
                                public void run() {
                                    hud.dismissWithError(customer.errmsg);
                                }
                            }, 100);

                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    customerSendSucess(customer);
                                }
                            }, 1000);
                        }
                    }

                });
        subscriptions.add(subscribe);
    }

    /**
     * 数据提交成功关闭页面
     *
     * @param retCustomer
     */
    public void customerSendSucess(Customer retCustomer) {
        if (actionType == TYPE_CLUE_TO_CUSTOMER) {
            Toast("转移成功");
            setResult(RESULT_OK);
        }
        MyCustomerRushEvent event = new MyCustomerRushEvent(retCustomer);
        event.eventCode = MyCustomerRushEvent.EVENT_CODE_ADD;
        AppBus.getInstance().post(event);
        isSave = false;//已经提交到服务器，不需要本地保存
        onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.Instance().deleteCustomer();
        if (isSave) {
            customer.name = edt_name.getText().toString().trim();
            Locate loc = new Locate();
            customer.loc = loc;
            customer.loc.addr = edit_address_details.getText().toString().trim();
            customer.summary = edt_content.getText().toString().trim();
            customer.webSite = edt_customer_weburl.getText() + "";
            //获取联系人号码
            Contact contact = new Contact();
            contact.setName(edt_contract.getText().toString());
            getTelNum(edt_contract_tel1, contact.telGroup);
            getTelNum(edt_contract_tel2, contact.telGroup);
            getTelNum(edt_contract_tel3, contact.telGroup);
            getTelNum(edt_contract_telnum1, contact.wiretelGroup);
            getTelNum(edt_contract_telnum2, contact.wiretelGroup);
            getTelNum(edt_contract_telnum3, contact.wiretelGroup);
            customer.contacts.clear();//先吧原来的清空
            customer.contacts.add(contact);
            DBManager.Instance().putCustomer(MainApp.gson.toJson(customer));
        }
        subscriptions.unsubscribe();
        locationGd.sotpLocation();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) {
            return;
        }
        switch (requestCode) {
            //地址微调
            case MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    if (null == customer.position) {
                        customer.position = new Locate();
                    }
                    customer.position.loc[0] = positionResultItem.laPosition;
                    customer.position.loc[1] = positionResultItem.loPosition;
                    et_address.setText(positionResultItem.address);
                    edit_address_details.setText(positionResultItem.address);
                }
                break;

            case REQUEST_CUSTOMER_SERACH:

                Bundle bundle1 = data.getExtras();
                edt_name.setText(bundle1.getString("name"));

                break;
            //客户状态
            case REQUEST_ACTIVITY_CODE_STATUS:
                CustomerStatusModel.CustomerStatusItemModel itemModel = (CustomerStatusModel.CustomerStatusItemModel) data.getSerializableExtra("data");
                customer.statusId = itemModel.id;
                customer.statusName = itemModel.name;
                tv_status.setText(itemModel.name);
                break;

            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                customer.tags = (ArrayList<NewTag>) bundle.getSerializable("data");
                tv_labels.setText(customer.displayTagString());
                break;



            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
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

            default:
                break;
        }
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
            attachmentBatch.bizType = LoyoBizType.Customer.getCode();
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        subscriptions.add(AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        requestCommitTask();
                    }
                }));
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
            requestCommitTask();
        }
    }
}