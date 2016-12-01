package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.event.ContactMaillistRushEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.ContactRequestParam;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
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

import org.greenrobot.eventbus.Subscribe;

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

    private TextView tv_phone_name1;
    private TextView tv_phone_name2;
    private TextView tv_phone_name3;

    private TextView tv_call_name1;
    private TextView tv_call_name2;
    private TextView tv_call_name3;

    private EditText edt_contract_tel1;
    private EditText edt_contract_tel2;
    private EditText edt_contract_tel3;

    private EditText edt_contract_telnum1;
    private EditText edt_contract_telnum2;
    private EditText edt_contract_telnum3;

    private ImageView iv_phone_insert1;
    private ImageView iv_phone_insert2;
    private ImageView iv_phone_insert3;

    private ImageView iv_call_insert1;
    private ImageView iv_call_insert2;
    private ImageView iv_call_insert3;

    private LinearLayout ll_phone_layout2;
    private LinearLayout ll_phone_layout3;
    private LinearLayout ll_call_layout2;
    private LinearLayout ll_call_layout3;

    private ImageView img_maillist_contact;
    private EditText et_name;

    private ArrayList<String> wiretelGroup;
    private ArrayList<String> telGroup;

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

    /**
     * Ui初始化
     */
    void initUI() {
        et_name = (EditText) findViewById(R.id.et_name);

        tv_phone_name1 = (TextView) findViewById(R.id.tv_phone_name1);
        tv_phone_name2 = (TextView) findViewById(R.id.tv_phone_name2);
        tv_phone_name3 = (TextView) findViewById(R.id.tv_phone_name3);

        tv_call_name1 = (TextView) findViewById(R.id.tv_call_name1);
        tv_call_name2 = (TextView) findViewById(R.id.tv_call_name2);
        tv_call_name3 = (TextView) findViewById(R.id.tv_call_name3);

        edt_contract_tel1 = (EditText) findViewById(R.id.edt_contract_tel1);
        edt_contract_tel2 = (EditText) findViewById(R.id.edt_contract_tel2);
        edt_contract_tel3 = (EditText) findViewById(R.id.edt_contract_tel3);

        edt_contract_telnum1 = (EditText) findViewById(R.id.edt_contract_telnum1);
        edt_contract_telnum2 = (EditText) findViewById(R.id.edt_contract_telnum2);
        edt_contract_telnum3 = (EditText) findViewById(R.id.edt_contract_telnum3);

        iv_phone_insert1 = (ImageView) findViewById(R.id.iv_phone_insert1);
        iv_phone_insert2 = (ImageView) findViewById(R.id.iv_phone_insert2);

        iv_call_insert1 = (ImageView) findViewById(R.id.iv_call_insert1);
        iv_call_insert2 = (ImageView) findViewById(R.id.iv_call_insert2);

        ll_phone_layout2 = (LinearLayout) findViewById(R.id.ll_phone_layout2);
        ll_phone_layout3 = (LinearLayout) findViewById(R.id.ll_phone_layout3);
        ll_call_layout2 = (LinearLayout) findViewById(R.id.ll_call_layout2);
        ll_call_layout3 = (LinearLayout) findViewById(R.id.ll_call_layout3);

        img_maillist_contact = (ImageView) findViewById(R.id.img_maillist_contact);

        layout_contact_extra_info = (LinearLayout) findViewById(R.id.layout_contact_extra_info);
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);

        iv_phone_insert1.setOnClickListener(this);
        iv_phone_insert2.setOnClickListener(this);
        iv_call_insert1.setOnClickListener(this);
        iv_call_insert2.setOnClickListener(this);
        img_maillist_contact.setOnClickListener(this);

        img_title_right.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        getContactsFields();
    }

    /**
     * 如果为编辑状态,绑定电话,座机,与姓名数据
     * */
    void bindEditData(){
        et_name.setText(mContact.getName());

        /*绑定电话数据*/
        if(null != mContact.telGroup){
            switch (mContact.telGroup.size()) {

                case 1:
                    edt_contract_tel1.setText(mContact.telGroup.get(0));
                    break;

                case 2:
                    iv_phone_insert1.setVisibility(View.INVISIBLE);
                    ll_phone_layout2.setVisibility(View.VISIBLE);
                    tv_phone_name1.setText("手机1");
                    tv_phone_name2.setText("手机2");
                    edt_contract_tel1.setText(mContact.telGroup.get(0));
                    edt_contract_tel2.setText(mContact.telGroup.get(1));
                    break;

                case 3:
                    iv_phone_insert1.setVisibility(View.INVISIBLE);
                    iv_phone_insert2.setVisibility(View.INVISIBLE);
                    ll_phone_layout2.setVisibility(View.VISIBLE);
                    ll_phone_layout3.setVisibility(View.VISIBLE);
                    tv_phone_name1.setText("手机1");
                    tv_phone_name2.setText("手机2");
                    tv_phone_name3.setText("手机3");
                    edt_contract_tel1.setText(mContact.telGroup.get(0));
                    edt_contract_tel2.setText(mContact.telGroup.get(1));
                    edt_contract_tel3.setText(mContact.telGroup.get(2));
                    break;

            }
        }

        /*绑定座机数据*/
        if(null != mContact.wiretelGroup){
            switch (mContact.wiretelGroup.size()) {

                case 1:
                    edt_contract_telnum1.setText(mContact.wiretelGroup.get(0));
                    break;

                case 2:
                    iv_call_insert1.setVisibility(View.INVISIBLE);
                    ll_call_layout2.setVisibility(View.VISIBLE);
                    tv_call_name1.setText("座机1");
                    tv_call_name2.setText("座机2");
                    edt_contract_telnum1.setText(mContact.wiretelGroup.get(0));
                    edt_contract_telnum2.setText(mContact.wiretelGroup.get(1));
                    break;

                case 3:
                    iv_call_insert1.setVisibility(View.INVISIBLE);
                    iv_call_insert2.setVisibility(View.INVISIBLE);
                    ll_call_layout2.setVisibility(View.VISIBLE);
                    ll_call_layout3.setVisibility(View.VISIBLE);
                    tv_call_name1.setText("座机1");
                    tv_call_name2.setText("座机2");
                    tv_call_name3.setText("座机3");
                    edt_contract_telnum1.setText(mContact.wiretelGroup.get(0));
                    edt_contract_telnum2.setText(mContact.wiretelGroup.get(1));
                    edt_contract_telnum3.setText(mContact.wiretelGroup.get(2));
                    break;

            }
        }
    }

    /**
     * 绑定数据
     */
    void bindData() {
        if(null != mContact){
            bindEditData();
        }
        layout_contact_extra_info.addView(new ContactAddforExtraData(mContext, mContact, mContactLeftExtras, true, 0));
    }

    /**
     * 通讯录导入回调
     * */
    @Subscribe
    public void onContactMaillistRushEvent(ContactMaillistRushEvent event){

        String name  = event.bundle.getString(ExtraAndResult.EXTRA_NAME);
        String phone = event.bundle.getString(ExtraAndResult.EXTRA_DATA);

        et_name.setText(name);
        edt_contract_tel1.setText(phone.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", ""));

    }

    /**
     * 获取手机号 电话号
     * */
    public void getTelNum(EditText editText,ArrayList<String> arrayList){
        if(!TextUtils.isEmpty(editText.getText().toString())){
            arrayList.add(editText.getText().toString());
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*通讯录导入*/
            case R.id.img_maillist_contact:
                boolean isEdit = false;
                if(!TextUtils.isEmpty(et_name.getText().toString())
                        || !TextUtils.isEmpty(edt_contract_tel1.getText().toString())){
                    isEdit = true;
                }
                Intent mIntent = new Intent();
                mIntent.setClass(CustomerContractAddActivity.this, MyContactMailList.class);
                mIntent.putExtra(ExtraAndResult.EXTRA_NAME,1);
                mIntent.putExtra(ExtraAndResult.EXTRA_OBJ,isEdit);
                startActivity(mIntent);
                break;

            /*手机添加1*/
            case R.id.iv_phone_insert1:
                tv_phone_name1.setText("手机1    ");
                tv_phone_name2.setText("手机2    ");
                iv_phone_insert1.setVisibility(View.INVISIBLE);
                ll_phone_layout2.setVisibility(View.VISIBLE);
                break;

            /*手机添加2*/
            case R.id.iv_phone_insert2:
                tv_phone_name3.setText("手机3    ");
                iv_phone_insert2.setVisibility(View.INVISIBLE);
                ll_phone_layout3.setVisibility(View.VISIBLE);
                break;

            /*电话添加1*/
            case R.id.iv_call_insert1:
                tv_call_name1.setText("座机1    ");
                tv_call_name2.setText("座机2    ");
                iv_call_insert1.setVisibility(View.INVISIBLE);
                ll_call_layout2.setVisibility(View.VISIBLE);
                break;

            /*电话添加2*/
            case R.id.iv_call_insert2:
                tv_call_name3.setText("座机3    ");
                iv_call_insert2.setVisibility(View.INVISIBLE);
                ll_call_layout3.setVisibility(View.VISIBLE);
                break;

            /*返回*/
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            /*提交*/
            case R.id.img_title_right:
                String name = et_name.getText().toString().trim();
                HashMap<String, Object> maps = new HashMap<>();

                /**姓名,手机,座机view写死,非空判断单独处理*/
                for (ContactLeftExtras contactLeftExtras : mContactLeftExtras) {
                    if(contactLeftExtras.fieldName.equals("name") && contactLeftExtras.required){
                        if(TextUtils.isEmpty(name)){
                            Toast("姓名不能为空");
                            return;
                        }
                    }
                }

                for (ContactLeftExtras contactLeftExtras : mContactLeftExtras) {
                    if (contactLeftExtras.required && TextUtils.isEmpty(contactLeftExtras.val)) {
                        if (contactLeftExtras.fieldName.equals("name")) {
                            continue;
                        }else if (contactLeftExtras.fieldName.equals("wiretel")) {
                            continue;
                        }else if (contactLeftExtras.fieldName.equals("tel")) {
                            continue;
                        }
                        Toast(contactLeftExtras.label + "不能为空");
                        return;
                    }
                    if (!contactLeftExtras.isSystem) {
                        LogUtil.dee(contactLeftExtras.label + ":" + contactLeftExtras.val);
                        contactRequestParam = new ContactRequestParam();
                        contactRequestParam.setVal(contactLeftExtras.val);
                        contactRequestParam.properties = contactLeftExtras;
                        contactRequestParam.required = contactLeftExtras.required;
                        requestContactParam.add(contactRequestParam);
                    } else {
                        switch (contactLeftExtras.fieldName) {

                            /*case "name":
                                maps.put("name", contactLeftExtras.val);
                                break;*/

                           /* case "tel":
                                if (null != contactLeftExtras.val && !contactLeftExtras.val.isEmpty())
                                    maps.put("tel", contactLeftExtras.val.replaceAll(" +", ""));
                                break;

                            case "wiretel":
                                if (null != contactLeftExtras.val && !contactLeftExtras.val.isEmpty())
                                    maps.put("wiretel", contactLeftExtras.val.replaceAll(" +", ""));
                                break;*/

                            case "birth":
                                if (TextUtils.isEmpty(contactLeftExtras.val)) {
                                    try {
                                        maps.put("birth", mContact.getBirthStr());
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    maps.put("birth", contactLeftExtras.val);
                                }
                                break;

                            case "dept_name":
                                maps.put("deptName", contactLeftExtras.val);
                                break;

                            case "qq":
                                maps.put("qq", contactLeftExtras.val);
                                break;

                            case "wx":
                                maps.put("wx", contactLeftExtras.val);
                                break;

                            case "email":
                                if (!TextUtils.isEmpty(contactLeftExtras.val)) {
                                    if (!RegularCheck.checkEmail(contactLeftExtras.val)) {
                                        Toast("Email格式不正确");
                                        return;
                                    }
                                }
                                maps.put("email", contactLeftExtras.val);
                                break;

                            case "memo":
                                maps.put("memo", contactLeftExtras.val);
                                break;

                        }
                    }
                }

                wiretelGroup = new ArrayList<>();
                telGroup = new ArrayList<>();

                getTelNum(edt_contract_tel1,telGroup);
                getTelNum(edt_contract_tel2,telGroup);
                getTelNum(edt_contract_tel3,telGroup);
                getTelNum(edt_contract_telnum1,wiretelGroup);
                getTelNum(edt_contract_telnum2,wiretelGroup);
                getTelNum(edt_contract_telnum3,wiretelGroup);

                maps.put("name", name);
                maps.put("telGroup", telGroup);
                maps.put("wiretelGroup", wiretelGroup);
                maps.put("extDatas", requestContactParam);

                LogUtil.dee("添加联系人发送map：" + MainApp.gson.toJson(maps));

               if (mCustomer != null) {
                    if (mContact == null) {
                        /*新建联系人*/
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
