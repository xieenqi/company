package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.bean.CallBackResp;
import com.loyo.oa.v2.activityui.customer.bean.CallClientInfo;
import com.loyo.oa.v2.activityui.customer.bean.CallUserResp;
import com.loyo.oa.v2.activityui.customer.bean.CustomerClientBean;
import com.loyo.oa.v2.activityui.customer.bean.PhoneCallBack;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.tool.RestAdapterPhoneFactory;
import com.loyo.oa.v2.tool.SharedUtil;
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
    @Extra(ExtraAndResult.EXTRA_ID)
    String customerId;
    @Extra("isMyUser")
    boolean isMyUser;
    @Extra("isRoot")
    boolean isRoot;
    @Extra("isLock")
    boolean isLock;
    @Extra(ExtraAndResult.EXTRA_STATUS)
    boolean isMenber;

    private Customer customerContact;
    private ArrayList<ContactLeftExtras> leftExtrases;

    private String contactId;
    private String contactName;
    private String callNum;
    private String myCall;

    @AfterViews
    void initViews() {
        if (!isLock) {
            layout_add.setVisibility(View.GONE);
        } else if (!isMyUser || isMenber) {
            if (!isRoot) {
                layout_add.setVisibility(View.GONE);
            }
        }
        setTouchView(NO_SCROLL);
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
        showLoading("");
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
                        HttpErrorCheck.checkError(error);
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
                HttpErrorCheck.checkResponse("联系人详情：", response);
                customerContact = customer;
                initData();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == leftExtrases || null == customerContact || null == customerContact.contacts
                || customerContact.contacts.isEmpty()) {
            return;
        }

        layout_container.removeAllViews();
        ArrayList<Contact> contacts = customerContact.contacts;
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            ContactViewGroup contactViewGroup = new ContactViewGroup(this, customerContact, leftExtrases, contact, this);
            contactViewGroup.bindView(i + 1, layout_container, isMyUser, isMenber, isRoot, isLock);
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


    /**
     * 获取Client信息
     */
    void requestClientInfo() {
        SharedUtil.clearInfo(mContext);
        SharedUtil.put(mContext, "time", DateTool.getNowTime("yyyyMMddHHmmss"));
        String stamp = SharedUtil.get(mContext, "time");

        String appId = "8b2cf8e33d48420f9fd99440bfebb0c9";      /*应用ID*/
        String token = "ab32202908271224d6ef12f73c3b701c";      /*token*/
        String accountSid = "e1d5b6a43b0bd8c47d13ec15af6671c4"; /*账户ID*/
        String sigParameter = accountSid + token + stamp;       /*账号ID+授权令牌+当前时间戳*/
        String sigParameterMD5 = Utils.md5(sigParameter);

        HashMap<String, Object> map = new HashMap<>();
        map.put("appId", appId);
        map.put("mobile", myCall);

        RestAdapterPhoneFactory.getInstance(mContext).build("https://api.ucpaas.com/2014-06-30/").create(ICustomer.class).getClientInfo(accountSid, sigParameterMD5.toUpperCase(), map,
                new RCallback<CallClientInfo>() {
                    @Override
                    public void success(final CallClientInfo callClientInfo, final Response response) {
                        HttpErrorCheck.checkResponse("查询Client", response);
                        if (null != callClientInfo) {
                            String code = callClientInfo.resp.respCode;
                            if (code.equals("100007")) {
                                requestClient();
                            } else if (code.equals("000000")) {
                                requestCallBack(callClientInfo.resp.client.clientNumber);
                            }
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
     * 通知服务端回拨成功
     */
    void toastServer(String callId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("callId", callId);
        map.put("customerId", customerContact.getId());
        map.put("contactId", contactId);
        map.put("contactName", contactName);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).toastOurServer(map,
                new RCallback<CallBackCallid>() {
                    @Override
                    public void success(final CallBackCallid callBackCallid, final Response response) {
                        HttpErrorCheck.checkResponse("通知服务端", response);
                        if (null != callBackCallid) {
                            int code = callBackCallid.errcode;
                            if (code == 0) {
                                Toast("电话回拨成功，请稍等..");
                            } else {
                                Toast("错误:" + code);
                            }
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
     * 绑定Client账号
     */
    void requestClient() {
        SharedUtil.clearInfo(mContext);
        SharedUtil.put(mContext, "time", DateTool.getNowTime("yyyyMMddHHmmss"));
        String stamp = SharedUtil.get(mContext, "time");

        String appId = "8b2cf8e33d48420f9fd99440bfebb0c9";      /*应用ID*/
        String token = "ab32202908271224d6ef12f73c3b701c";      /*token*/
        String accountSid = "e1d5b6a43b0bd8c47d13ec15af6671c4"; /*账户ID*/
        String sigParameter = accountSid + token + stamp;       /*账号ID+授权令牌+当前时间戳*/
        String sigParameterMD5 = Utils.md5(sigParameter);

        CustomerClientBean ccb = new CustomerClientBean();
        ccb.friendlyName = "";
        ccb.appId = appId;
        ccb.charge = "10";
        ccb.mobile = myCall;
        ccb.clientType = "0";

        HashMap<String, Object> map = new HashMap<>();
        map.put("client", ccb);
        LogUtil.d("请求Client绑定 发送数据:" + MainApp.gson.toJson(ccb));

        RestAdapterPhoneFactory.getInstance(mContext).build("https://api.ucpaas.com/2014-06-30/").create(ICustomer.class).getClient(accountSid, sigParameterMD5.toUpperCase(), map,
                new RCallback<CallUserResp>() {
                    @Override
                    public void success(final CallUserResp callUserResp, final Response response) {
                        HttpErrorCheck.checkResponse("申请Client", response);
                        if (null != callUserResp) {
                            String code = callUserResp.resp.respCode;
                            if (code.equals("000000") || code.equals("103114")) {
                                requestCallBack(callUserResp.resp.client.clientNumber);
                            } else {
                                Toast("错误:" + callUserResp.resp.respCode);
                            }
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    private String getPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    /**
     * 请求电话回拨
     */
    void requestCallBack(String client) {
        SharedUtil.clearInfo(mContext);
        SharedUtil.put(mContext, "time", DateTool.getNowTime("yyyyMMddHHmmss"));
        String stamp = SharedUtil.get(mContext, "time");

        String appId = "8b2cf8e33d48420f9fd99440bfebb0c9";      /*应用ID*/
        String token = "ab32202908271224d6ef12f73c3b701c";      /*token*/
        String accountSid = "e1d5b6a43b0bd8c47d13ec15af6671c4"; /*账户ID*/
        String sigParameter = accountSid + token + stamp;       /*账号ID+授权令牌+当前时间戳*/
        String sigParameterMD5 = Utils.md5(sigParameter);

        PhoneCallBack callBack = new PhoneCallBack();
        callBack.appId = appId;                              /*应用ID*/
        callBack.fromClient = client;                        /*主叫号码*/
        callBack.to = callNum;                               /*被叫号码*/
        callBack.fromSerNum = "999";                         /*主叫号码显号*/
        callBack.toSerNum = MainApp.user.mobile;                           /*被叫号码显号*/
        callBack.maxallowtime = "60";                        /*允许呼叫时长*/
        callBack.ringtoneID = "";                            /*彩铃Id*/
        callBack.userData = "";                              /*自定义透传数据*/

        HashMap<String, Object> map = new HashMap<>();
        map.put("callback", callBack);
        LogUtil.d("电话回拨 发送数据:" + MainApp.gson.toJson(map));

        RestAdapterPhoneFactory.getInstance(mContext).build("https://api.ucpaas.com/2014-06-30/").create(ICustomer.class).getCallBack(accountSid, sigParameterMD5.toUpperCase(), map,
                new RCallback<CallBackResp>() {
                    @Override
                    public void success(final CallBackResp callBackResp, final Response response) {
                        HttpErrorCheck.checkResponse("请求回拨", response);
                        if (null != callBackResp) {
                            if (callBackResp.resp.respCode.equals("000000")) {
                                toastServer(callBackResp.resp.callback.callId);
                            } else {
                                Toast("错误:" + callBackResp.resp.respCode);
                            }
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
    public void onCallBack(String callNum, String contactId, String contactName) {
        this.callNum = callNum;
        this.contactId = contactId;
        this.contactName = contactName;
        myCall = MainApp.user.mobile;
        LogUtil.dee("我的号码:" + myCall);
        LogUtil.dee("被叫号码:" + callNum);
        LogUtil.dee("contactId:" + contactId);
        LogUtil.dee("contactName:" + contactName);
        requestClientInfo();
        app.startActivity(CustomerContactManageActivity.this, CustomerPhoneActivity.class, MainApp.ENTER_TYPE_RIGHT, false, new Bundle());
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
                        LogUtil.dll("onDel");
                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.equals(contact)) {
                                LogUtil.dll("if -- onDel");
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
                                initData();
                                break;
                            }
                        }
                    }
                });
    }
}
