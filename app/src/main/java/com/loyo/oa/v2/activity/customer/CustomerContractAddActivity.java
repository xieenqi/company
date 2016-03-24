package com.loyo.oa.v2.activity.customer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.ContactExtras;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.ExtraDataViewforContact;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户管理联系人列表，【新增联系人】
 */

public class CustomerContractAddActivity extends BaseActivity implements View.OnClickListener {

    ViewGroup img_title_left;
    ViewGroup img_title_right;
    Customer mCustomer;
    Contact mContact;
    LinearLayout layout_contact_extra_info;
    public ArrayList<ContactExtras> mContactExtras;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_contract_add);

        super.setTitle("新增联系人");

        if (getIntent() != null && getIntent().getExtras() != null) {
            mCustomer = (Customer) getIntent().getExtras().getSerializable("customer");
            mContact = (Contact) getIntent().getExtras().getSerializable("contract");
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
     * */
    void bindData(){
        LogUtil.d("bindData");
        layout_contact_extra_info.addView(new ExtraDataViewforContact(mContext,mContactExtras,true,R.color.title_bg1, 0));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:

/*                String name = edt_name.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast("联系人不能为空");
                    return;
                }
                if (phone.isEmpty()) {
                    Toast("手机号不能为空");
                    return;
                } else {//验证电话号码
                    if (!RegularCheck.isMobilePhone(phone)) {
                        Toast("手机号码格式不正确");
                        return;
                    }
                }
                //如果有输入座机就验证座机
                if (!TextUtils.isEmpty(getEditTextContent(edt_wiretel))) {
                    if (!RegularCheck.isPhone(getEditTextContent(edt_wiretel))) {
                        Toast("座机号码格式不正确");
                        return;
                    }
                }
                //邮箱验证
                if (!TextUtils.isEmpty(getEditTextContent(edt_email))) {
                    if (!RegularCheck.checkEmail(getEditTextContent(edt_email))) {
                        Toast("邮箱格式不正确");
                        return;
                    }
                }

                HashMap<String, Object> maps = new HashMap<>();
                maps.put("name", getContractName());
                maps.put("tel", getContractTel());
                maps.put("wiretel", getEditTextContent(edt_wiretel));
                String birth = edt_birth.getText().toString().trim();
                if (!TextUtils.isEmpty(birth)) {
                    maps.put("birth", birth);
                }// DateTool.getDateToTimestamp(birth, app.df4) / 1000+""
                maps.put("wx", getEditTextContent(edt_wx));
                maps.put("qq", getEditTextContent(edt_qq));
                maps.put("email", getEditTextContent(edt_email));
                maps.put("memo", getEditTextContent(edt_memo));
                LogUtil.d("添加联系人：" + MainApp.gson.toJson(maps));*/

                HashMap<String, Object> maps = new HashMap<>();
                maps.put("name", mContactExtras.get(0).getVal());
                maps.put("tel", mContactExtras.get(1).getVal());
                maps.put("wiretel", mContactExtras.get(2).getVal());
                maps.put("birth", mContactExtras.get(3).getVal());
                maps.put("wx", mContactExtras.get(4).getVal());
                maps.put("qq", mContactExtras.get(5).getVal());
                maps.put("email", mContactExtras.get(6).getVal());
                maps.put("memo", mContactExtras.get(7).getVal());
                LogUtil.d("添加联系人：" + MainApp.gson.toJson(maps));

                if (mCustomer != null) {
                    if (mContact == null) {
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addContact(mCustomer.getId(), maps, new RCallback<Contact>() {
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
                        /*修改联系人*/
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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getContactsField(new RCallback<ArrayList<ContactExtras>>() {
            @Override
            public void success(ArrayList<ContactExtras> ContactExtras, Response response) {
                HttpErrorCheck.checkResponse("联系人动态字段", response);
                LogUtil.d("联系人动态字段 toJson:" + MainApp.gson.toJson(ContactExtras));
                mContactExtras = ContactExtras;
                bindData();
            }

            @Override
            public void failure(RetrofitError error){
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Toast("请求失败");
            }
        });
    }

    void sendBack() {
        Intent intent = new Intent();
        intent.putExtra("data", mContact);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }
}
