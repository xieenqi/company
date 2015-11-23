package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

/**
 * com.loyo.oa.v2.activity
 * 描述 :客户基础信息（名称、备注）编辑类
 * 作者 : ykb
 * 时间 : 15/7/17.
 */
@EActivity(R.layout.activity_customer_baseinfo_edit_layout)
public class CustomerBaseInfoEditActivity extends BaseActivity
{
    @ViewById
    EditText edt_customer_basesummary;
    @ViewById
    EditText edt_basename;

    @Extra("Customer") Customer customer;

    @AfterViews
     void initUi()
    {
        ((TextView) findViewById(R.id.tv_title_1)).setText("客户基础信息");
        if(!TextUtils.isEmpty(customer.getSummary()))
            edt_customer_basesummary.setText(customer.getSummary());
        if(!TextUtils.isEmpty(customer.getName()))
            edt_basename.setText(customer.getName());
    }

    @Click({R.id.img_title_left, R.id.img_title_right})
     void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_title_left:
                MainApp.getMainApp().finishActivity(this, MainApp.ENTER_TYPE_RIGHT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:
                String name=getEditTextContent(edt_basename);
                String content=getEditTextContent(edt_customer_basesummary);
                    JSONObject jsonObject = new JSONObject();
                    StringEntity stringEntity = null;
                    try {
                        jsonObject.put("name", name);
                        jsonObject.put("content", content);
                        //如果不传默认的联系人的话,服务端会把默认联系人更新掉。
                        Contact contact= Utils.findDeault(customer);
                        if(null!=contact) {
                            jsonObject.put("contactPersonName", contact.getName());
                            jsonObject.put("contactPersonTel", contact.getTel());
                        }

                        stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
                    } catch (Exception e) {
                        Global.ProcException(e);
                    }
                    ServerAPI.request(this, ServerAPI.PUT, FinalVariables.customers + customer.getId(), stringEntity, ServerAPI.CONTENT_TYPE_JSON, AsyncUpdateCustomer.class);
                break;
        }

    }

    public class AsyncUpdateCustomer extends BaseActivityAsyncHttpResponseHandler {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Customer retCustomer = MainApp.gson.fromJson(getStr(arg2), Customer.class);
                if(retCustomer!=null) {
                    Toast(getString(R.string.app_update) + getString(R.string.app_succeed));
                    customer = retCustomer;
                    Intent intent = new Intent();
                    intent.putExtra("customer", customer);
                    MainApp.getMainApp().finishActivity(CustomerBaseInfoEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

    /**
     * 获取EditText里的内容
     * @param et EditText
     * @return EditText里的内容
     */
    private String getEditTextContent(EditText et) {
        return et.getText().toString().trim();
    }
}
