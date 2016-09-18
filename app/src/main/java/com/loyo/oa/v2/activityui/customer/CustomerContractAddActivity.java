package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.bean.ContactRequestParam;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.ContactAddforExtraData;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户管理联系人列表，【新增联系人】
 */

public class CustomerContractAddActivity extends BaseActivity implements View.OnClickListener {

    public ViewGroup img_title_left;
    public ViewGroup img_title_right;
    public Customer mCustomer;
    public Contact mContact;
    public LinearLayout layout_contact_extra_info;
    public ContactRequestParam contactRequestParam;
    public ArrayList<ContactLeftExtras> mContactLeftExtras;
    public ArrayList<ContactRequestParam> requestContactParam = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_contract_add);
        super.setTitle("新增联系人");
        if (getIntent() != null && getIntent().getExtras() != null) {
            mCustomer = (Customer) getIntent().getExtras().getSerializable("customer");
            mContact = (Contact) getIntent().getExtras().getSerializable("contract");
            if (null != mContact)
                super.setTitle("编辑联系人");
        }
        initUI();
    }

    void initUI() {
        layout_contact_extra_info = (LinearLayout) findViewById(R.id.layout_contact_extra_info);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        getContactsFields();
    }

    /**
     * 绑定数据
     */
    void bindData() {
        layout_contact_extra_info.addView(new ContactAddforExtraData(mContext, mContact, mContactLeftExtras, true, R.color.title_bg1, 0));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:
                HashMap<String, Object> maps = new HashMap<>();
                for (ContactLeftExtras contactLeftExtras : mContactLeftExtras) {
                    if (!contactLeftExtras.isSystem) {
                        LogUtil.dee(contactLeftExtras.label + ":" + contactLeftExtras.val);
                        contactRequestParam = new ContactRequestParam();
                        contactRequestParam.setVal(contactLeftExtras.val);
                        contactRequestParam.properties = contactLeftExtras;
                        contactRequestParam.required = contactLeftExtras.required;
                        requestContactParam.add(contactRequestParam);
                    } else {
                        if (contactLeftExtras.fieldName.equals("name")) {

                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("姓名不能为空");
                                return;
                            }
                            maps.put("name", contactLeftExtras.val);

                        } else if (contactLeftExtras.fieldName.equals("tel")) {

                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("手机号不能为空");
                                return;
                            }
// else {
//                                if (!RegularCheck.isMobilePhone(contactLeftExtras.val)) {
//                                    Toast("手机号码格式不正确");
//                                    return;
//                                }
//                            }
                            maps.put("tel", contactLeftExtras.val);

                        } else if (contactLeftExtras.fieldName.equals("wiretel")) {
                            if (contactLeftExtras.required && TextUtils.isEmpty(contactLeftExtras.val)) {
                                Toast("座机号码不能为空");
                                return;
//                                if (!RegularCheck.isPhone(contactLeftExtras.val)) {//验证座机号码
//                                }
                            }
                            maps.put("wiretel", contactLeftExtras.val);

                        } else if (contactLeftExtras.fieldName.equals("birth")) {
                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("生日必选");
                                return;
                            }
                            if (TextUtils.isEmpty(contactLeftExtras.val)) {
                                try {
                                    maps.put("birth", mContact.getBirthStr());
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                maps.put("birth", contactLeftExtras.val);
                            }
                        } else if (contactLeftExtras.fieldName.equals("dept_name")) {
                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("部门职务不能为空");
                                return;
                            }
                            maps.put("deptName", contactLeftExtras.val);
                        } else if (contactLeftExtras.fieldName.equals("qq")) {
                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("QQ不能为空");
                                return;
                            }
                            maps.put("qq", contactLeftExtras.val);
                        } else if (contactLeftExtras.fieldName.equals("wx")) {
                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("微信不能为空");
                                return;
                            }
                            maps.put("wx", contactLeftExtras.val);
                        } else if (contactLeftExtras.fieldName.equals("email")) {
                            if (!TextUtils.isEmpty(contactLeftExtras.val)) {
                                if (!RegularCheck.checkEmail(contactLeftExtras.val)) {
                                    Toast("Email格式不正确");
                                    return;
                                }
                            } else if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("Email不能为空");
                                return;
                            }
                            maps.put("email", contactLeftExtras.val);

                        } else if (contactLeftExtras.fieldName.equals("memo")) {
                            if (contactLeftExtras.required && contactLeftExtras.val.isEmpty()) {
                                Toast("备注不能为空");
                                return;
                            }
                            maps.put("memo", contactLeftExtras.val);
                        }
                    }
                }
                for (ContactRequestParam ele : requestContactParam) {
                    if (ele.required && TextUtils.isEmpty(ele.properties.val)) {
                        Toast(ele.properties.label + "不能为空");
                        return;
                    }
                }
                maps.put("extDatas", requestContactParam);
                LogUtil.d("添加联系人发送map：" + MainApp.gson.toJson(maps));

                if (mCustomer != null) {
                    if (mContact == null) {
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                                addContact(mCustomer.getId(), maps, new RCallback<Contact>() {
                                    @Override
                                    public void success(final Contact contact, final Response response) {
                                        HttpErrorCheck.checkResponse("添加联系人：", response);
                                        mContact = contact;
                                        sendBack();
                                    }

                                    @Override
                                    public void failure(final RetrofitError error) {
                                        HttpErrorCheck.checkError(error);
                                    }
                                });
                    } else {
                        //*修改联系人*//
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                                create(ICustomer.class).updateContact(mCustomer.getId(), mContact.getId(), maps, new RCallback<Contact>() {
                            @Override
                            public void success(final Contact contact, final Response response) {
                                HttpErrorCheck.checkResponse("修改联系人", response);
                                mContact = contact;
                                sendBack();
                            }

                            @Override
                            public void failure(final RetrofitError error) {
                                HttpErrorCheck.checkError(error);
                            }
                        });
                    }
                }
                break;

            default:
                break;
        }
    }

    /**
     * 获取联系人的动态字段
     */
    private void getContactsFields() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getContactsField(new RCallback<ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(ArrayList<ContactLeftExtras> ContactLeftExtras, Response response) {
                HttpErrorCheck.checkResponse("联系人动态字段", response);
                mContactLeftExtras = ContactLeftExtras;
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    void sendBack() {
        Intent intent = new Intent();
        intent.putExtra("data", mContact);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }
}
