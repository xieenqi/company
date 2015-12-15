package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.CustomerRegional;
import com.loyo.oa.v2.beans.Industry;
import com.loyo.oa.v2.beans.Locate;
import com.loyo.oa.v2.beans.Member;
import com.loyo.oa.v2.beans.NewTag;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.OnMenuSelectCallback;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.ExtraDataView;
import com.loyo.oa.v2.tool.customview.multi_level_interaction_menu.DialogFragmentAreaCast;
import com.loyo.oa.v2.tool.customview.multi_level_interaction_menu.DialogFragmentIndustryCast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【客户信息】 页面
 */
@EActivity(R.layout.activity_customer_info)
public class CustomerInfoActivity extends BaseFragmentActivity implements LocationUtil.AfterLocation {

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_NEW_CONTRACT = 6;
    public static final int REQUEST_CUSTOMER_UPDATE_CONTRACT = 7;
    public static final int REQUEST_CUSTOMER_FOLLOW = 8;
    public static final int REQUEST_CUSTOMER_EDIT_BASEINFO = 9;

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ImageView imgview_title_right;

    @ViewById
    ViewGroup layout_customer_district;
    @ViewById
    ViewGroup layout_customer_industry;
    @ViewById
    ViewGroup layout_customer_label;
    @ViewById
    ViewGroup layout_customer_responser;
    @ViewById
    ViewGroup layout_customer_join_users;


    @ViewById(R.id.layout_customer_extra_info)
    LinearLayout container;

    @ViewById
    EditText tv_address;

    @ViewById
    EditText tv_customer_name;
    @ViewById
    TextView tv_customer_creator;
    @ViewById
    TextView tv_customer_responser;
    @ViewById
    TextView tv_customer_join_users;
    @ViewById
    TextView tv_customer_create_at;
    @ViewById
    EditText edt_customer_memo;

    @ViewById
    TextView tv_labels;
    @ViewById
    TextView tv_industry;
    @ViewById
    TextView tv_district;

    LinearLayout layout_rushpackger;

    @ViewById
    ImageView img_go_where;
    @ViewById
    ImageView img_refresh_address;
    @ViewById
    ImageView img_del_join_users;

    @Extra("Customer")
    Customer mCustomer;

    @Extra("isMyUser")
    boolean isMyUser;

    @Extra("CustomerId")
    String mCustomerId;


    private double lat, lng;

    ArrayList<NewTag> mTagItems = new ArrayList<>();
    private Locate mLocate = new Locate();
    private Member owner = new Member();
    private ArrayList<Member> members = new ArrayList<>();
    private CustomerRegional regional = new CustomerRegional();
    private Industry industry = new Industry();

    private Animation animation;

    @AfterViews
    void initUI() {
        layout_rushpackger = (LinearLayout) findViewById(R.id.layout_rushpackger);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        img_refresh_address.setOnTouchListener(Global.GetTouch());
        img_go_where.setOnTouchListener(Global.GetTouch());
        layout_customer_label.setOnTouchListener(Global.GetTouch());
        layout_customer_responser.setOnTouchListener(Global.GetTouch());
        layout_customer_join_users.setOnTouchListener(Global.GetTouch());
        img_del_join_users.setOnTouchListener(Global.GetTouch());
        layout_customer_district.setOnTouchListener(Global.GetTouch());
        layout_customer_industry.setOnTouchListener(Global.GetTouch());
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);

        if (isMyUser == false) {
            imgview_title_right.setVisibility(View.GONE);
        }
        ((TextView) findViewById(R.id.tv_title_1)).setText("客户信息");
        getCustomer();
    }

    /**
     * 获取用户信息
     */
    void getCustomer() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(mCustomer == null ? mCustomerId : mCustomer.getId(), new RCallback<Customer>() {
            @Override
            public void success(Customer customer, Response response) {
                mCustomer = customer;
                initData();
            }
        });
    }

    /**
     * 初始化动态字段
     */
    private void initExtra(boolean ismy) {
        if (null != mCustomer.getExtDatas() && !mCustomer.getExtDatas().isEmpty()) {
            container.setVisibility(View.VISIBLE);
            container.addView(new ExtraDataView(mContext, mCustomer.getExtDatas(), ismy, R.color.title_bg1, 0));
        }
    }

    void initData() {

        /*如果不是自己的客户，不允许操作*/
        if (!isMyUser) {
            layout_rushpackger.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
            img_refresh_address.setVisibility(View.GONE);
            tv_customer_name.setEnabled(false);
            tv_address.setEnabled(false);
            edt_customer_memo.setEnabled(false);
            layout_customer_industry.setEnabled(false);
            layout_customer_district.setEnabled(false);
            layout_customer_label.setEnabled(false);
            layout_customer_responser.setEnabled(false);
            layout_customer_join_users.setEnabled(false);
            img_refresh_address.setEnabled(false);
        }
        initExtra(isMyUser);

        if (mCustomer == null) {
            return;
        }

        if (null != mCustomer.getMembers()) {
            members = mCustomer.getMembers();
        }

        if (null != mCustomer.getLoc()) {
            mLocate = mCustomer.getLoc();
        }

        if (null != mCustomer.getOwner()) {
            owner = mCustomer.getOwner();
        }

        if (null != mCustomer.getRegional()) {
            regional = mCustomer.getRegional();
        }

        if (null != mCustomer.getIndustry()) {
            industry = mCustomer.getIndustry();
        }


        if (null != mCustomer.getLoc() && mCustomer.getLoc().getLoc().length > 1) {
            lat = mCustomer.getLoc().getLoc()[1];
            lng = mCustomer.getLoc().getLoc()[0];
        }

        tv_customer_name.setText(mCustomer.getName());

        if (mCustomer.getLoc().getAddr() != "") {
            tv_address.setText(mCustomer.getLoc().getAddr());
        } else {
            Intent intent = new Intent();
            Bundle bundle = intent.getExtras();
            tv_address.setText(bundle.getString("CustomerAddress"));
        }

        tv_customer_creator.setText(mCustomer.getCreator().getName());
        String responser = null == mCustomer.getOwner() || null == mCustomer.getOwner().getUser() ? "" : mCustomer.getOwner().getUser().getName();
        tv_customer_responser.setText(responser);

        if (members.size() != 0) {
            img_del_join_users.setVisibility(View.VISIBLE);
            tv_customer_join_users.setText(Utils.getMembers(members));
        }

        if (regional.getProvince() != null) {
            tv_district.setText(regional.getProvince() + "省" + regional.getCity() + "市" + regional.getCounty() + "区");
        }
        tv_industry.setText(industry.getName());
        edt_customer_memo.setText(mCustomer.getSummary());
        tv_customer_create_at.setText(app.df3.format(new Date(mCustomer.getCreatedAt() * 1000)));

        if (mCustomer.getTags() != null && mCustomer.getTags().size() > 0) {
            mTagItems = mCustomer.getTags();
            setTag();
        }


    }

    @UiThread
    void setTag() {
        StringBuffer sb = null;

        for (NewTag item : mTagItems) {
            if (sb == null) {
                sb = new StringBuffer();
                sb.append(String.valueOf(item.getItemName()));
            } else {
                sb.append("/");
                sb.append(String.valueOf(item.getItemName()));
            }
        }

        if (sb != null) {
            tv_labels.setText(sb.toString());
        }
    }

    /**
     * 显示对话框
     */
    private void showLeaveDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("负责人更改后，此客户所有数据和管理权限将归属新的负责人，您确定要更改负责人？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_SINGLE);
                app.startActivityForResult(CustomerInfoActivity.this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, bundle);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        }).create();
        dialog.show();
    }


    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_label, R.id.img_refresh_address, R.id.img_go_where, R.id.img_del_join_users, R.id.layout_customer_responser, R.id.layout_customer_join_users, R.id.layout_customer_district, R.id.layout_customer_industry})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:

                if (tv_industry.getText().toString().isEmpty()) {
                    Toast("行业不能为空");
                } else if (tv_district.getText().toString().isEmpty()) {
                    Toast("地区不能为空");
                } else {
                    updateCustomer();
                }
                break;

            case R.id.layout_customer_label:
                Bundle bundle2 = new Bundle();
                if (mTagItems != null) {
                    bundle2.putSerializable("tagitems", Utils.convertTagItems(mTagItems));
                    bundle2.putString("customerId", mCustomer.getId());
                }
                app.startActivityForResult(this, CustomerLabelActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_LABEL, bundle2);
                break;

            case R.id.img_refresh_address:
                new LocationUtil(this, this);
                img_refresh_address.startAnimation(animation);
                break;

            case R.id.img_go_where:
                Utils.goWhere(this, lat, lng);
                break;

            case R.id.img_del_join_users:
                members.clear();
                tv_customer_join_users.setText("");
                img_del_join_users.setVisibility(View.GONE);
                break;

            case R.id.layout_customer_responser:
                showLeaveDialog();
                break;

            case R.id.layout_customer_join_users:
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, null);
                break;

            case R.id.layout_customer_district:
                new DialogFragmentAreaCast().show(getSupportFragmentManager(), "地区选择", new OnMenuSelectCallback() {
                    @Override
                    public void onMenuSelected(Object o) {
                        regional = (CustomerRegional) o;
                        tv_district.setText(regional.getProvince() + "省" + regional.getCity() + "市" + regional.getCounty() + "区");
                    }
                });
                break;

            case R.id.layout_customer_industry:
                new DialogFragmentIndustryCast().show(getSupportFragmentManager(), "行业选择", new OnMenuSelectCallback() {
                    @Override
                    public void onMenuSelected(Object o) {
                        industry = (Industry) o;
                        tv_industry.setText(industry.getName());
                    }
                });
                break;
        }
    }

    /**
     * 更新客戶
     */
    private void updateCustomer() {

        String customerName = tv_customer_name.getText().toString().trim();
        String customerAddress = tv_address.getText().toString().trim();
        String summary = edt_customer_memo.getText().toString().trim();


        if (TextUtils.isEmpty(customerName)) {
            Toast.makeText(this, "客户姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(customerAddress)) {
            Toast.makeText(this, "客户地址不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mLocate.setAddr(customerAddress);

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", customerName);
        map.put("summary", summary);
        map.put("owner", owner);
        map.put("members", members);
        map.put("tags", mTagItems);
        map.put("loc", mLocate);
        map.put("extDatas", mCustomer.getExtDatas());
        map.put("regional", regional);
        map.put("industry", industry);

        LogUtil.dll("id:"+mCustomer.getId());
        LogUtil.dll("name:" + customerName);
        LogUtil.dll("summary:" + summary);
        LogUtil.dll("owner:" + owner);
        LogUtil.dll("members:" + members);
        LogUtil.dll("tags:" + mTagItems);
        LogUtil.dll("loc:" + mLocate);
        LogUtil.dll("extDatas:"+ mCustomer.getExtDatas());
        LogUtil.dll("regional:"+regional);
        LogUtil.dll("industry:"+industry);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                updateCustomer(mCustomer.getId(), map, new RCallback<Customer>() {
                    @Override
                    public void success(Customer customer, Response response) {
                        Intent intent = new Intent();
                        customer.setLoc(mLocate);
                        intent.putExtra(Customer.class.getName(), customer);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                            LogUtil.dll("url:"+error.getUrl());

                        if (error.getKind() == RetrofitError.Kind.NETWORK) {
                            Toast.makeText(CustomerInfoActivity.this, "请检查您的网络连接", Toast.LENGTH_SHORT).show();
                        } else if (error.getResponse().getStatus() == 500) {

                            try {
                                JSONObject jsonObject = new JSONObject(Utils.convertStreamToString(error.getResponse().getBody().in()));
                                Toast.makeText(CustomerInfoActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                LogUtil.dll("error:" + Utils.convertStreamToString(error.getResponse().getBody().in()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    @Override
    public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
        img_refresh_address.clearAnimation();
        animation.reset();
        lat = latitude;
        lng = longitude;
        LogUtil.dll("onlocationSucessed:" + address);
        mLocate.setAddr(address);
        mLocate.getLoc()[0] = longitude;
        mLocate.getLoc()[1] = latitude;
        tv_address.setText(address);
    }

    @Override
    public void OnLocationFailed() {
        img_refresh_address.clearAnimation();
        animation.reset();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Customer.class.getName(), mCustomer);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case DepartmentUserActivity.request_Code:
                /*负责人人*/
                User user = (User) data.getSerializableExtra(User.class.getName());
                if (user != null) {
                    NewUser u = new NewUser();
                    u.setId(user.getId());
                    u.setName(user.getRealname());
                    owner.setUser(u);
                    tv_customer_responser.setText(u.getName());
                }
                //参与人
                else {
                    String userIds = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                    String userNames = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                    members = Utils.convert2Members(userIds, userNames);
                    if (members.size() != 0) {
                        img_del_join_users.setVisibility(View.VISIBLE);
                        tv_customer_join_users.setText(userNames);
                    }
                }
                break;

            case REQUEST_CUSTOMER_EDIT_BASEINFO:
                mCustomer = (Customer) data.getSerializableExtra("customer");
                tv_customer_name.setText(mCustomer.getName());
                break;
            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                mTagItems = (ArrayList<NewTag>) bundle.getSerializable("data");

                StringBuffer sb = null;

                for (NewTag item : mTagItems) {
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append(String.valueOf(item.getItemName()));
                    } else {
                        sb.append("/");
                        sb.append(String.valueOf(item.getItemName()));
                    }
                }

                if (sb != null) {
                    tv_labels.setText(sb.toString());
                } else {
                    tv_labels.setText("");
                }

                mCustomer.setTags(mTagItems);
                break;

        }
    }
}