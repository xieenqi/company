package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.DateTimePickDialog;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建跟进动态
 */
public class SaleActivitiesAddActivity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time, tv_customer, tv_contact_name;
    private Customer mCustomer;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_customer, ll_contact, ll_contactItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_activities_add);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
        }

        initUI();
        getTempSaleActivity();
    }

    void getTempSaleActivity() {
        //        mSaleActivity = DBManager.Instance().getSaleActivity(mCustomer.getId());
        if (mSaleActivity == null) {
            return;
        }

        edt.setText(mSaleActivity.getContent());
    }

    void initUI() {
        super.setTitle("写跟进");

        edt = (EditText) findViewById(R.id.edt);
        tv_remain_time = (TextView) findViewById(R.id.tv_remain_time);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);

        layout_remain_time = (ViewGroup) findViewById(R.id.layout_remain_time);
        layout_remain_time.setOnClickListener(this);
        layout_remain_time.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);

        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_customer.setOnClickListener(this);
        ll_customer.setOnTouchListener(touch);
        tv_customer = (TextView) findViewById(R.id.tv_customer);

        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_contact.setOnTouchListener(touch);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_customer.setVisibility(null == mCustomer ? View.VISIBLE : View.GONE);
        ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
        if(null!=mCustomer){
            getDefaultContact( mCustomer.contacts);
        }
    }

    /**
     * 获取客户的默认联系人
     * @param data
     */
    private void getDefaultContact(ArrayList<Contact> data){
    for(Contact ele:data){
        if(!ele.isDefault()){
            continue;
        }else {
            contactId=ele.getId();
            contactName=ele.getName();
            tv_contact_name.setText(contactName);
        }
    }
}
    /**
     * 选择下次跟进时间
     */
    private void selectRemainTime() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                tv_remain_time.setText(str);
            }

            @Override
            public void onCancel() {
                tv_remain_time.setText("不提醒");
            }
        }, false, "不提醒");
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.layout_remain_time:
                selectRemainTime();
                break;
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            case R.id.img_title_right:
                String content = edt.getText().toString().trim();

                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                } else if (null == mCustomer || TextUtils.isEmpty(mCustomer.getId())) {
                    Toast("请选择跟进客户");
                    return;
                }


                HashMap<String, Object> map = new HashMap<>();
                map.put("customerId", mCustomer.getId());
                map.put("content", content);
                map.put("typeId", tagItemIds);
                if (!tv_remain_time.getText().toString().isEmpty() || !tv_remain_time.getText().toString().equals("不提醒")) {
                    map.put("remindAt", DateTool.getDateToTimestamp(tv_remain_time.getText().toString().trim(), app.df2) / 1000);
                }
                if (!TextUtils.isEmpty(contactId)) {
                    map.put("contactId", contactId);
                    map.put("contactName", contactName);
                }
                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSaleactivity(map, new RCallback<SaleActivity>() {
                    @Override
                    public void success(final SaleActivity saleActivity, final Response response) {
                        HttpErrorCheck.checkResponse("新建跟进动态", response);
                        app.finishActivity(SaleActivitiesAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });

                break;
//选择客户
            case R.id.ll_customer:
                Bundle b = new Bundle();
                app.startActivityForResult(SaleActivitiesAddActivity.this, SigninSelectCustomer.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                break;
            //选择联系人
            case R.id.ll_contact:
                Bundle bContact = new Bundle();
                bContact.putSerializable(ExtraAndResult.EXTRA_DATA, mCustomer.contacts);
                bContact.putString(ExtraAndResult.EXTRA_NAME, tv_contact_name.getText().toString());
                app.startActivityForResult(SaleActivitiesAddActivity.this, FollowContactSelectActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, bContact);
                break;
        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCustomer) {
            DBManager.Instance().deleteSaleActivity(mCustomer.getId());
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), mCustomer.getId());
            }
        }
    }

    /**
     * 获取跟进方式
     *
     * @param tags
     * @return
     */
    private String getSaleTypes(final ArrayList<CommonTag> tags) {
        if (null == tags || tags.isEmpty()) {
            return "";
        }
        StringBuilder reasons = new StringBuilder();
        int index = 0;
        for (CommonTag reson : tags) {
            reasons.append(reson.getName());
            if (index < tags.size() - 1) {
                reasons.append(",");
            }
            index++;
        }
        return reasons.toString();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CommonTagSelectActivity.REQUEST_TAGS:
                ArrayList<CommonTag> tags = (ArrayList<CommonTag>) data.getSerializableExtra("data");
                tv_sale_action.setText(getSaleTypes(tags));
                tagItemIds = tags.get(0).getId();
                break;
            //选择客户返回
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                String customerName = "无";
                if (null != customer) {
                    mCustomer = customer;
                    customerName=customer.name;
                    getDefaultContact( mCustomer.contacts);
                }
                tv_customer.setText(customerName);
                ll_contactItem.setVisibility(null == mCustomer ? View.GONE : View.VISIBLE);
                break;
            //选择客户联系人
            case ExtraAndResult.REQUEST_CODE_STAGE:
                Contact contact = (Contact) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                if (null != contact) {
                    contactId = contact.getId();
                    contactName = contact.getName();
                }
                tv_contact_name.setText(contactName);
                break;
        }
    }
}
