package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :客户联系人管理界面【联系人详情】
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
@EActivity(R.layout.activity_customer_contact_manage)
public class CustomerContactManageActivity extends BaseActivity implements ContactViewGroup.OnContactProcessCallback {

    @ViewById
    ViewGroup layout_back;
    @ViewById
    TextView tv_title;
    @ViewById
    LinearLayout layout_container;
    @ViewById
    ViewGroup layout_add;
    @ViewById
    LoadingLayout ll_loading;

    @Extra(ExtraAndResult.EXTRA_ID)
    String customerId;
    @Extra("canEdit")
    boolean canEdit;

    private Customer customerContact;
    private ArrayList<ContactLeftExtras> leftExtrases;

    private String contactId;
    private String contactName;
    private String callNum;
    private String myCall;
    private int callType;

    private String phoneNum;//用来存储一下电话号码，用在动态授权上

    @AfterViews
    void initViews() {
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getContactsFields();
            }
        });
        layout_add.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("联系人详情");
        layout_back.setOnTouchListener(Global.GetTouch());
        layout_add.setOnTouchListener(Global.GetTouch());
        getContactsFields();
    }

    /**
     * 获取最新 左侧动态字段
     */
    private void getContactsFields() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getContactsField(new RCallback<ArrayList<ContactLeftExtras>>() {
                    @Override
                    public void success(ArrayList<ContactLeftExtras> fiedListData, Response response) {
                        HttpErrorCheck.checkResponse("联系人动态字段", response);
                        leftExtrases = fiedListData;
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error, ll_loading);
                    }
                });
    }

    /**
     * 获取客户详情
     */
    private void getData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerContacts(customerId, new RCallback<Customer>() {
            @Override
            public void success(final Customer customer, final Response response) {
                HttpErrorCheck.checkResponse("联系人详情：", response, ll_loading);
                customerContact = customer;
                initData();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error, ll_loading);
            }
        });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        /*if (null == leftExtrases || null == customerContact || null == customerContact.contacts
                || customerContact.contacts.isEmpty()) {
            ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }*/

        if (null == customerContact.contacts && customerContact.contacts.size() == 0) {
            ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }


        layout_container.removeAllViews();
        ArrayList<Contact> contactsCopy = new ArrayList<>();
        contactsCopy.clear();

        /*默认数据放在最前*/
        for (Contact mContact : customerContact.contacts) {
            if (mContact.isDefault()) {
                contactsCopy.add(mContact);
                break;
            }
        }
        /*非默认联系人排后*/
        for (Contact mContact : customerContact.contacts) {
            if (!mContact.isDefault()) {
                contactsCopy.add(mContact);
            }
        }

        for (int i = 0; i < contactsCopy.size(); i++) {
            Contact contact = contactsCopy.get(i);
            ContactViewGroup contactViewGroup = new ContactViewGroup(this, customerContact, leftExtrases, contact, this);
            contactViewGroup.bindView(i + 1, layout_container, canEdit);
        }

        cancelLoading();
    }


    @Override
    public void setPhone(String phone) {
        //用来保存刚才用户拨打的电话
        this.phoneNum = phone;
    }

    //用来处理打电话权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Utils.CALL_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.call(CustomerContactManageActivity.this, phoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了打电话权限，无法拨出电话");
                }
            });
        }else if(Utils.SEND_SMS_REQUEST==requestCode){
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                     Utils.sendSms(CustomerContactManageActivity.this, phoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了发短信权限，无法发送短信");
                }
            });
        }
    }

    @Click(R.id.layout_add)
    void addNewContact() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customerContact);
        app.startActivityForResult(this, CustomerContractAddActivity.class, MainApp.ENTER_TYPE_RIGHT, CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT, bundle);
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {

            case CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT:
                Contact contact = (Contact) data.getSerializableExtra("data");
                customerContact.contacts.add(contact);
                LogUtil.dee("contacts:"+MainApp.gson.toJson(customerContact.contacts));
                initData();
                break;

            case CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT:
                Contact contactUpdated = (Contact) data.getSerializableExtra("data");
                if (contactUpdated == null) {
                    return;
                }

                for (int i = 0; i < customerContact.contacts.size(); i++) {
                    if (TextUtils.equals(contactUpdated.getId(), customerContact.contacts.get(i).getId())) {
                        contactUpdated.setIsDefault(customerContact.contacts.get(i).isDefault());
                        customerContact.contacts.set(i, contactUpdated);
                        break;
                    }
                }
                initData();
                break;

            default:
                break;
        }
    }


    void requestClientInfo() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", customerContact.getId());
        map.put("contactId", contactId);
        map.put("type", callType);
        map.put("mobile", callNum);
        LogUtil.dee("请求回拨发送数据：" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).requestCallBack(map,
                new RCallback<CallBackCallid>() {
                    @Override
                    public void success(final CallBackCallid callBackCallid, final Response response) {
                        HttpErrorCheck.checkResponse("请求回拨", response);
                        try {
                            switch (callBackCallid.errcode) {
                                case 0:
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString(ExtraAndResult.WELCOM_KEY, callBackCallid.data.callLogId);
                                    mBundle.putString(ExtraAndResult.EXTRA_NAME, contactName);
                                    app.startActivity(CustomerContactManageActivity.this, CallPhoneBackActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                                    break;

                                case 50000:
                                    Toast("主叫与被叫号码不能相同!");
                                    break;

                                case 50001:
                                    Toast("余额不足!");
                                    break;

                                case 50002:
                                    Toast("号码格式错误!");
                                    break;

                                default:
                                    Toast("网络连接失败:" + callBackCallid.errcode);
                                    break;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast(e.getMessage());
                            finish();
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
     * 拨打商务电话回调
     */
    @Override
    public void onCallBack(String callNum, String contactId, String contactName, @ContactViewGroup.CallPhoneType int callType, int phoneType) {
        this.callNum = callNum;
        this.contactId = contactId;
        this.contactName = contactName;
        this.callType = phoneType;
        myCall = MainApp.user.mobile;
        LogUtil.dee("我的号码:" + myCall);
        LogUtil.dee("被叫号码:" + callNum);
        LogUtil.dee("contactId:" + contactId);
        LogUtil.dee("contactName:" + contactName);
//
        if (callType == ContactViewGroup.CallbackPhone) {
            requestClientInfo();
        }
    }

    @Override
    public void onPhoneError() {
        Toast("电话号码格式不正确或为空!");
    }


    /**
     * 删除联系人的回调 xnq
     *
     * @param contact
     */
    @Override
    public void onDel(final Contact contact) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                deleteContact(customerContact.getId(), contact.getId(), new RCallback<Contact>() {
                    @Override
                    public void success(Contact contact, Response response) {
                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.equals(contact)) {
                                customerContact.contacts.remove(i);
                                initData();
                                break;
                            }
                        }
                        refresh();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    private void refresh() {
        onCreate(null);
    }

    /**
     * 设置默认联系人的回调 xnq
     *
     * @param contact
     */
    @Override
    public void onSetDefault(final Contact contact) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                setDefaultContact(customerContact.getId(), contact.getId(), new RCallback<Contact>() {
                    @Override
                    public void success(final Contact _contact, final Response response) {
                        HttpErrorCheck.checkResponse("设置默认联系人", response);
                        Intent intent = new Intent();
                        CustomerContactManageActivity.this.setResult(Activity.RESULT_OK, intent);//回调刷新界面

                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.isDefault()) {
                                newContact.setIsDefault(false);
                                break;
                            }
                        }
                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.equals(contact)) {
                                newContact.setIsDefault(true);
                                customerContact.contacts.set(i, newContact);
                                break;
                            }
                        }
                        initData();
                    }
                });
    }
}
