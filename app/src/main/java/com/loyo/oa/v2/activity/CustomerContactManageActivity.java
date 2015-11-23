package com.loyo.oa.v2.activity;

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
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.ContactViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :客户联系人管理界面
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

    @AfterViews
    void initViews() {
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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                mCustomer = customer;
                initData();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == mCustomer || null == mCustomer.getContacts() || mCustomer.getContacts().isEmpty()) {
            return;
        }
        layout_container.removeAllViews();
        ArrayList<Contact> contacts = mCustomer.getContacts();
        app.logUtil.e("contacts : " + contacts.size());
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            ContactViewGroup contactViewGroup = new ContactViewGroup(this,mCustomer, contact,this);
            contactViewGroup.bindView(i + 1, layout_container);
        }
    }



    @Click(R.id.layout_add)
    void addNewContact() {
        Bundle bundle=new Bundle();
        bundle.putSerializable("customer",mCustomer);
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
                mCustomer.getContacts().add(contact);
                initData();
                break;
            case CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT:
                Contact contactUpdated = (Contact) data.getSerializableExtra("data");
                if (contactUpdated == null) {
                    return;
                }

                for (int i = 0; i < mCustomer.getContacts().size(); i++) {
                    if (TextUtils.equals(contactUpdated.getId(), mCustomer.getContacts().get(i).getId())) {
                        contactUpdated.setIsDefault(mCustomer.getContacts().get(i).isDefault());
                        mCustomer.getContacts().set(i, contactUpdated);
                        break;
                    }
                }
                initData();
                break;
        }
    }

    @Override
    public void onDel(final Contact contact) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).deleteContact(mCustomer.getId(), contact.getId(), new RCallback<Contact>() {
            @Override
            public void success(Contact contact, Response response) {
                for (int i = 0; i < mCustomer.getContacts().size(); i++) {
                    Contact newContact=mCustomer.getContacts().get(i);
                    if(newContact.equals(contact)){
                       mCustomer.getContacts().remove(i);
                        initData();
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onSetDefault(final Contact contact) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).setDefaultContact(mCustomer.getId(), contact.getId(), new RCallback<Contact>() {
            @Override
            public void success(Contact _contact, Response response) {
                for (int i = 0; i < mCustomer.getContacts().size(); i++) {
                    Contact newContact=mCustomer.getContacts().get(i);
                    if(newContact.isDefault()){
                        newContact.setIsDefault(false);
                        break;
                    }
                }
                for (int i = 0; i < mCustomer.getContacts().size(); i++) {
                    Contact newContact=mCustomer.getContacts().get(i);
                    if(newContact.equals(contact)){
                        newContact.setIsDefault(true);
                        mCustomer.getContacts().set(i,newContact);
                        initData();
                        break;
                    }
                }
            }
        });
    }
}
