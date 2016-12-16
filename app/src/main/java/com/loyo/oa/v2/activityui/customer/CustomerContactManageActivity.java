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
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.network.DefaultSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.voip.VoIPCallActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;

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
    private int    callType;

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
        layout_add.setVisibility(canEdit?View.VISIBLE:View.GONE);
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
        CustomerService.getContactsField()
                .subscribe(new DefaultSubscriber<ArrayList<ContactLeftExtras>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        super.onNext(contactLeftExtrasArrayList);
                        leftExtrases = contactLeftExtrasArrayList;
                        getData();
                    }
                });
    }

    /**
     * 获取客户详情
     */
    private void getData() {
        CustomerService.getCustomerContacts(customerId)
                .subscribe(new DefaultSubscriber<Customer>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ll_loading.setStatus(LoadingLayout.Empty);
                    }

                    @Override
                    public void onNext(Customer customer) {
                        super.onNext(customer);
                        customerContact = customer;
                        initData();
                    }
                });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        if (null == leftExtrases || null == customerContact || null == customerContact.contacts
                || customerContact.contacts.isEmpty()) {
            ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }
        ll_loading.setStatus(LoadingLayout.Success);

        layout_container.removeAllViews();
        ArrayList<Contact> contactsCopy = new ArrayList<>();
        contactsCopy.clear();

        /*默认数据放在最前*/
        for(Contact mContact : customerContact.contacts){
            if(mContact.isDefault()){
                contactsCopy.add(mContact);
                break;
            }
        }
        /*非默认联系人排后*/
        for(Contact mContact : customerContact.contacts){
            if(!mContact.isDefault()){
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
        LogUtil.dee("请求回拨发送数据："+MainApp.gson.toJson(map));
        CustomerService.requestCallBack(map)
                .subscribe(new DefaultSubscriber<CallBackCallid>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(CallBackCallid callBackCallid) {
                        super.onNext(callBackCallid);
                        try{
                            switch (callBackCallid.errcode){
                                case 0:
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString(ExtraAndResult.WELCOM_KEY,callBackCallid.data.callLogId);
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
                                    Toast("网络连接失败:"+callBackCallid.errcode);
                                    break;
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                            Toast(e.getMessage());
                            finish();
                        }
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
        }else if (callType == ContactViewGroup.DirectPhone)  {
            Bundle mBundle = new Bundle();
            mBundle.putString(VoIPCallActivity.CALLEE_PHONE_KEY,callNum);
            mBundle.putString(VoIPCallActivity.CALLEE_NAME_KEY, contactName);
            mBundle.putString(VoIPCallActivity.CALLEE_USER_KEY, contactId);
            mBundle.putString(VoIPCallActivity.CALLEE_CUSTOMER_KEY, customerContact.getId());
            mBundle.putInt(VoIPCallActivity.CALLEE_USER_TYPE, callType);
            app.startActivity(CustomerContactManageActivity.this, VoIPCallActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
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
        CustomerService.deleteContact(customerContact.getId(), contact.getId())
                .subscribe(new DefaultSubscriber<Contact>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Contact contact1) {
                        super.onNext(contact1);
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
        CustomerService.setDefaultContact(customerContact.getId(), contact.getId())
                .subscribe(new DefaultSubscriber<Contact>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Contact contact1) {
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
