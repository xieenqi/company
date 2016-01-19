package com.loyo.oa.v2.activity.customer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import org.apache.http.Header;

import java.util.Calendar;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 客户管理联系人列表，【新增联系人】
 * */

public class CustomerContractAddActivity extends BaseActivity implements View.OnClickListener {

    ViewGroup img_title_left;
    ViewGroup img_title_right;
    EditText edt_name, edt_phone, edt_qq, edt_email, edt_memo, edt_wx, edt_wiretel;
    TextView edt_birth;

    ViewGroup layout_extra;

    Customer mCustomer;
    Contact mContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_qq = (EditText) findViewById(R.id.edt_qq);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_memo = (EditText) findViewById(R.id.edt_remark);
        edt_wx = (EditText) findViewById(R.id.edt_wx);
        edt_wiretel = (EditText) findViewById(R.id.edt_wiretel);
        edt_birth = (TextView) findViewById(R.id.edt_birthday);

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

        layout_extra = (ViewGroup) findViewById(R.id.contact_extra_layout);

        edt_birth.setOnTouchListener(Global.GetTouch());
        edt_birth.setOnClickListener(this);

        if (mContact != null) {
            edt_name.setText(mContact.getName());
            edt_phone.setText(mContact.getTel());
            edt_qq.setText(mContact.getQq());
            edt_wx.setText(mContact.getWx());
            edt_wiretel.setText(mContact.getWiretel());
            edt_birth.setText(mContact.getBirthStr());
            edt_email.setText(mContact.getEmail());
            edt_memo.setText(mContact.getMemo());

            super.setTitle("修改联系人");
        } else {
            //            layout_extra.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:
                String name = edt_name.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast("联系人不能为空");
                    return;
                }
                if (phone.isEmpty()) {
                    Toast("手机号不能为空");
                    return;
                }else{//验证电话号码
                    if(!RegularCheck.isMobilePhone(phone)){
                        Toast("手机号码格式不正确");
                        return;
                    }
                }
                //如果有输入座机就验证座机
                if(!TextUtils.isEmpty(getEditTextContent(edt_wiretel))){
                    if(!RegularCheck.isPhone(getEditTextContent(edt_wiretel))){
                        Toast("座机号码格式不正确");
                        return;
                    }
                }
                //邮箱验证
                if(!TextUtils.isEmpty(getEditTextContent(edt_email))){
                    if(!RegularCheck.checkEmail(getEditTextContent(edt_email))){
                        Toast("邮箱格式不正确");
                        return;
                    }
                }
                //现有客户新增联系人
                HashMap<String, Object> maps = new HashMap<>();
                maps.put("name", getContractName());
                maps.put("tel", getContractTel());
                maps.put("wiretel", getEditTextContent(edt_wiretel));
                String birth = edt_birth.getText().toString().trim();
                if (!TextUtils.isEmpty(birth)) {
                    maps.put("birth",birth);
                }// DateTool.getDateToTimestamp(birth, app.df4) / 1000+""
                maps.put("wx", getEditTextContent(edt_wx));
                maps.put("qq", getEditTextContent(edt_qq));
                maps.put("email", getEditTextContent(edt_email));
                maps.put("memo", getEditTextContent(edt_memo));
                LogUtil.d("添加联系人："+MainApp.gson.toJson(maps));
                if (mCustomer != null) {
                    if (mContact == null) {
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addContact(mCustomer.getId(), maps, new RCallback<Contact>() {
                            @Override
                            public void success(Contact contact, Response response) {
                                mContact = contact;
                                sendBack();
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                LogUtil.d("客户 新增联系人URL"+error.getUrl());
                                LogUtil.d("客户 新增联系人"+error.getMessage());

                                if(error.getKind() == RetrofitError.Kind.NETWORK){
                                    Toast("请检查您的网络连接");
                                }
                                else if(error.getResponse().getStatus() == 500){
                                    Toast("网络异常500,请稍候再试");
                                }
                            }
                        });
                    } else {
                        /*修改联系人*/
                        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).updateContact(mCustomer.getId(), mContact.getId(), maps, new RCallback<Contact>() {
                            @Override
                            public void success(Contact contact, Response response) {
                                mContact = contact;
                                sendBack();
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                LogUtil.d("客户 修改联系人:"+error.getMessage());
                                LogUtil.d("客户 修改联系人URL:"+error.getUrl());

                                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                                    Toast("请检查您的网络连接");
                                } else if (error.getResponse().getStatus() == 500) {
                                    Toast("网络异常500,请稍候再试");
                                }
                            }

                        });
                    }

                }
                break;
            case R.id.edt_birthday:
                selectBirthDay();
                break;
        }
    }

    /**
     * 设置生日
     */
    private void selectBirthDay() {
        DateTool.calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int age = Utils.getAge(year + "");
                if (age > 0) {
                    String str = year + "." + String.format("%02d", (monthOfYear + 1)) + "." + String.format("%02d", dayOfMonth);
                    Utils.setContent(edt_birth, str);
                } else {
                    Toast("请输入您正确的生日");
                }
            }
        }, DateTool.calendar.get(Calendar.YEAR), DateTool.calendar.get(Calendar.MONTH), DateTool.calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    Contact getmContact() {
        Contact contract = new Contact();
        contract.setName(getContractName());
        contract.setTel(getContractTel());

        if (mContact != null) {
            contract.setId(mContact.getId());
        }

        return contract;
    }

    /**
     * 获取EditText里的内容
     *
     * @param et 需要获取数据的EditText
     * @return EditText里的内容
     */
    private String getEditTextContent(EditText et) {
        return et.getText().toString().trim();
    }

    String getContractName() {
        return edt_name.getText().toString().trim();
    }

    String getContractTel() {
        return edt_phone.getText().toString().trim();
    }

    void sendBack() {
        Intent intent = new Intent();
        intent.putExtra("data", mContact);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    public class AsyncHandler_contract extends BaseActivityAsyncHttpResponseHandler {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Contact contact = MainApp.gson.fromJson(getStr(arg2), Contact.class);
                if (contact != null) {
                    mContact = contact;
                }
                //应传回完整对象
                sendBack();

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

}
