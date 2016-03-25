package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.ContactExtras;
import com.loyo.oa.v2.beans.ContactRequestParam;
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
import com.loyo.oa.v2.tool.customview.ExtraDataViewforContact;
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
    public ArrayList<ContactExtras> mContactExtras;
    public ArrayList<ContactRequestParam> requestContactParam = new ArrayList<>();

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
     */
    void bindData() {
        layout_contact_extra_info.addView(new ExtraDataViewforContact(mContext,mContact,mContactExtras, true, R.color.title_bg1, 0));
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:

                HashMap<String, Object> maps = new HashMap<>();
                for(ContactExtras contactExtras : mContactExtras){
                    if(contactExtras.fieldName.length() >20){
                        contactRequestParam = new ContactRequestParam();
                        contactRequestParam.setVal(contactExtras.val);
                        contactRequestParam.properties = contactExtras;
                        requestContactParam.add(contactRequestParam);
                    }else{
                        if(contactExtras.fieldName.equals("name")){

                            if(contactExtras.val.isEmpty()){
                                Toast("姓名不能为空");
                                return;
                            }
                            maps.put("name", contactExtras.val);

                        }else if(contactExtras.fieldName.equals("wiretel")){

                            if(!contactExtras.val.isEmpty()){
                                   if(!RegularCheck.isPhone(contactExtras.val)){
                                       Toast("座机号码格式不正确");
                                       return;
                                   }
                            }
                            maps.put("wiretel", contactExtras.val);

                        }else if(contactExtras.fieldName.equals("tel")){

                            if(contactExtras.val.isEmpty()){
                                Toast("手机号不能为空");
                                return;
                            }else{
                                if(!RegularCheck.isMobilePhone(contactExtras.val)){
                                    Toast("手机号码格式不正确");
                                    return;
                                }
                            }
                            maps.put("tel", contactExtras.val);

                        }else if(contactExtras.fieldName.equals("birth")){
                            if(contactExtras.val.isEmpty()){
                                maps.put("birth",mContact.getBirthStr());
                            }else{
                                maps.put("birth", contactExtras.val);
                            }
                        }else if(contactExtras.fieldName.equals("wx")){
                            maps.put("wx", contactExtras.val);
                        }else if(contactExtras.fieldName.equals("qq")){
                            maps.put("qq", contactExtras.val);
                        }else if(contactExtras.fieldName.equals("email")){
                            if(!contactExtras.val.isEmpty()){
                                if(!RegularCheck.checkEmail(contactExtras.val)){
                                    Toast("Email格式不正确");
                                    return;
                                }
                            }
                            maps.put("email", contactExtras.val);

                        }else if(contactExtras.fieldName.equals("memo")) {
                            maps.put("memo", contactExtras.val);
                        }else if(contactExtras.fieldName.equals("dept_name")){
                            maps.put("deptName",contactExtras.val);
                        }
                    }
                }
                maps.put("extDatas",requestContactParam);

                LogUtil.d("原始map数据:" + MainApp.gson.toJson(mContactExtras));
                LogUtil.d("添加联系人发送map：" + MainApp.gson.toJson(maps));

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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getContactsField(new RCallback<ArrayList<ContactExtras>>() {
            @Override
            public void success(ArrayList<ContactExtras> ContactExtras, Response response) {
                HttpErrorCheck.checkResponse("联系人动态字段", response);
                LogUtil.d("联系人动态字段 toJson:" + MainApp.gson.toJson(ContactExtras));
                mContactExtras = ContactExtras;
                bindData();
            }

            @Override
            public void failure(RetrofitError error) {
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
