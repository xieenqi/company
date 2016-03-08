package com.loyo.oa.v2.activity.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.ContactViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

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

    @ViewById ViewGroup layout_back;
    @ViewById TextView tv_title;
    @ViewById LinearLayout layout_container;
    @ViewById ViewGroup layout_add;
    @Extra("Customer") Customer mCustomer;
    @Extra("isMyUser") boolean isMyUser;
    @Extra(ExtraAndResult.EXTRA_STATUS) boolean isMenber;

    @AfterViews
    void initViews() {

        if (!isMyUser || isMenber) {
            layout_add.setVisibility(View.GONE);
        }

        setTouchView(NO_SCROLL);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("联系人详情");
        layout_back.setOnTouchListener(Global.GetTouch());
        layout_add.setOnTouchListener(Global.GetTouch());
        getData();

    }

    /**
     * 获取客户详情
     */
    private void getData() {
        Utils.dialogShow(this,"请稍候");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                mCustomer = customer;
                Utils.dialogDismiss();
                initData();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Utils.dialogDismiss();
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == mCustomer || null == mCustomer.contacts || mCustomer.contacts.isEmpty()) {
            return;
        }
        layout_container.removeAllViews();
        ArrayList<Contact> contacts = mCustomer.contacts;
        app.logUtil.e("contacts : " + contacts.size());
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            ContactViewGroup contactViewGroup = new ContactViewGroup(this, mCustomer, contact, this);
            contactViewGroup.bindView(i + 1, layout_container, isMyUser ,isMenber);
        }
    }


    @Click(R.id.layout_add)
    void addNewContact() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", mCustomer);
        app.startActivityForResult(this, CustomerContractAddActivity.class, MainApp.ENTER_TYPE_RIGHT, CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT, bundle);
    }

    @Click(R.id.layout_back)
    void back() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {
            case CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT:
                Contact contact = (Contact) data.getSerializableExtra("data");
                mCustomer.contacts.add(contact);
                initData();
                break;
            case CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT:
                Contact contactUpdated = (Contact) data.getSerializableExtra("data");
                if (contactUpdated == null) {
                    return;
                }

                for (int i = 0; i < mCustomer.contacts.size(); i++) {
                    if (TextUtils.equals(contactUpdated.getId(), mCustomer.contacts.get(i).getId())) {
                        contactUpdated.setIsDefault(mCustomer.contacts.get(i).isDefault());
                        mCustomer.contacts.set(i, contactUpdated);
                        break;
                    }
                }
                initData();
                break;
        }
    }

    /**
     * 删除联系人的回调 xnq
     *
     * @param contact
     */
    @Override
    public void onDel(final Contact contact) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                deleteContact(mCustomer.getId(), contact.getId(), new RCallback<Contact>() {
                    @Override
                    public void success(Contact contact, Response response) {
                        LogUtil.dll("onDel");
                        for (int i = 0; i < mCustomer.contacts.size(); i++) {
                            Contact newContact = mCustomer.contacts.get(i);
                            if (newContact.equals(contact)) {
                                LogUtil.dll("if -- onDel");
                                mCustomer.contacts.remove(i);
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
                setDefaultContact(mCustomer.getId(), contact.getId(), new RCallback<Contact>() {
                    @Override
                    public void success(Contact _contact, Response response) {
                        Intent intent = new Intent();
                        CustomerContactManageActivity.this.setResult(Activity.RESULT_OK, intent);//回调刷新界面
                        for (int i = 0; i < mCustomer.contacts.size(); i++) {
                            Contact newContact = mCustomer.contacts.get(i);
                            if (newContact.isDefault()) {
                                newContact.setIsDefault(false);
                                break;
                            }
                        }
                        for (int i = 0; i < mCustomer.contacts.size(); i++) {
                            Contact newContact = mCustomer.contacts.get(i);
                            if (newContact.equals(contact)) {
                                newContact.setIsDefault(true);
                                mCustomer.contacts.set(i, newContact);
                                initData();
                                break;
                            }
                        }
                    }
                });
    }
}
