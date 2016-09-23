package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
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
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.bean.CustomerExtraData;
import com.loyo.oa.v2.activityui.customer.bean.CustomerRegional;
import com.loyo.oa.v2.activityui.customer.bean.ExtraData;
import com.loyo.oa.v2.activityui.customer.bean.Locate;
import com.loyo.oa.v2.activityui.customer.bean.Member;
import com.loyo.oa.v2.activityui.customer.bean.NewTag;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CustomerInfoExtraData;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【客户信息】 页面
 */
@EActivity(R.layout.activity_customer_info)
public class CustomerInfoActivity extends BaseFragmentActivity {

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
    ViewGroup layout_customer_district;
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
    TextView tv_district;
    LinearLayout layout_rushpackger;
    @ViewById
    ImageView img_go_where;
    @ViewById
    ImageView img_refresh_address;
    @ViewById
    ImageView img_del_join_users;
    @ViewById
    EditText edt_address_details;

    @Extra("Customer")
    Customer mCustomer;
    @Extra("isMyUser")
    boolean isMyUser;
    @Extra(ExtraAndResult.EXTRA_TYPE)
    boolean isPublic;
    @Extra(ExtraAndResult.EXTRA_STATUS)
    boolean isMenber;
    @Extra("CustomerId")
    String mCustomerId;
    @Extra("isRoot")
    boolean isRoot;

    private boolean isMem = false;
    private String addres;
    private Bundle mBundle;
    private ArrayList<NewTag> mTagItems = new ArrayList<>();
    private Locate mLocate = new Locate();
    private User owner = new User();
    private ArrayList<Member> members = new ArrayList<>();
    private Members cusMembers = new Members();
    private CustomerRegional regional = new CustomerRegional();
    //    private Industry industry = new Industry();
    private StringBuffer mManagerIds = new StringBuffer();
    private StringBuffer mManagerNames = new StringBuffer();
    private ArrayList<CustomerExtraData> mCustomerExtraDatas;
    private PositionResultItem positionResultItem;

    private double laPosition;//当前位置的经纬度
    private double loPosition;
    private boolean cusLocation = false;//定位权限
    private boolean cusDetialAdress = false;//客户的详细地址
    private boolean cusBrief = false;//客户简介

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
        ((TextView) findViewById(R.id.tv_title_1)).setText("客户信息");
        getCustomer();
    }

    /**
     * 获取客户动态字段
     */
    void getExtraData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 100);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getDynamic(map, new RCallback<ArrayList<CustomerExtraData>>() {
                    @Override
                    public void success(final ArrayList<CustomerExtraData> customerExtraDatas, final Response response) {
                        HttpErrorCheck.checkResponse("客户动态字段", response);
                        mCustomerExtraDatas = customerExtraDatas;
                        initData();
                        Utils.dialogDismiss();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                        Utils.dialogDismiss();
                        finish();
                    }
                });
        requestJurisdiction();
    }

    /**
     * 获取新建客户权限
     */
    public void requestJurisdiction() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 100);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getAddCustomerJur(map, new RCallback<ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(final ArrayList<ContactLeftExtras> cuslist, final Response response) {
                HttpErrorCheck.checkResponse("编辑客户那些字段必填权限", response);
                for (ContactLeftExtras customerJur : cuslist) {
                    if (customerJur.label.contains("简介") && customerJur.required) {
                        cusBrief = true;
                        edt_customer_memo.setHint("请输入客户简介(必填)");
                    } else if (customerJur.label.contains("定位") && customerJur.required) {
                        tv_address.setHint("客户地址(必填)");
                        cusLocation = true;//定位必填
                    } else if (customerJur.label.contains("客户地址") && customerJur.required) {
                        cusDetialAdress = true;//详细地址必填
                        edt_address_details.setHint("请输入客户详细地址(必填)");
                    }
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Utils.dialogDismiss();
            }
        });
    }

    /**
     * 获取用户信息
     */
    void getCustomer() {
        Utils.dialogShow(this, "请稍候");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getCustomerById(null == mCustomer ? mCustomerId : mCustomer.getId(), new RCallback<Customer>() {
                    @Override
                    public void success(final Customer customer, final Response response) {
                        HttpErrorCheck.checkResponse("客户信息", response);
                        mCustomer = customer;
                        getExtraData();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                        Utils.dialogDismiss();
                        finish();
                    }
                });
    }

    /**
     * 显示地区选择Dialog
     */
    void loadAreaCodeTable() {
        String[] cityValue = null;
        if (!tv_district.getText().toString().isEmpty()) {
            cityValue = tv_district.getText().toString().split(" ");
        }
        final SelectCityView selectCityView = new SelectCityView(this, cityValue);
        selectCityView.setCanceledOnTouchOutside(true);
        selectCityView.show();
        selectCityView.setOnclickselectCity(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] cityArr = selectCityView.getResult();
                tv_district.setText(cityArr[0] + " " + cityArr[1] + " " + cityArr[2]);
                regional.province = cityArr[0];
                regional.city = cityArr[1];
                regional.county = cityArr[2];
                selectCityView.dismiss();
            }
        });
    }

    /**
     * 初始化动态字段
     */
    private void initExtra(final boolean ismy) {
        /*动态字段 数据转换*/
        if (null != mCustomer.extDatas && !mCustomer.extDatas.isEmpty()) {
            for (ExtraData extraData : mCustomer.extDatas) {
                for (CustomerExtraData customerExtraData : mCustomerExtraDatas) {
                    if (extraData.getProperties().getName().equals(customerExtraData.getName())) {
                        extraData.getProperties().setEnabled(customerExtraData.isEnabled());
                        extraData.getProperties().setRequired(customerExtraData.isRequired());
                        extraData.getProperties().setLabel(customerExtraData.getLabel());
                    }
                }
            }
            container.setVisibility(View.VISIBLE);
            container.addView(new CustomerInfoExtraData(mContext, mCustomer.extDatas, ismy, R.color.title_bg1, 0, isRoot, isMenber, mCustomer.lock));
        }
    }

    void initData() {
        initExtra(isMyUser);
        /*公海客户*/
        if (!mCustomer.lock) {
            setEnable();
            /*如果不是自己的客户，不允许操作*/
        } else if (!isMyUser || isMenber) {
            if (!isRoot) {
                setEnable();
            } else {
                layout_customer_responser.setEnabled(false);
                layout_customer_join_users.setEnabled(false);
                tv_customer_responser.setTextColor(getResources().getColor(R.color.md_grey_500));
                tv_customer_join_users.setTextColor(getResources().getColor(R.color.md_grey_500));
            }
        }

        if (mCustomer == null) {
            return;
        }

        if (null != mCustomer.members) {
            members = mCustomer.members;
        }

        if (null != mCustomer.position) {
            mLocate = mCustomer.position;
        }

        if (null != mCustomer.owner) {
            owner = mCustomer.owner;
        }

        if (null != mCustomer.regional) {
            regional = mCustomer.regional;
        }
//
//        if (null != mCustomer.industry) {
//            industry = mCustomer.industry;
//        }

        try {
            if (null != mCustomer.position && mCustomer.position.loc.length > 1) {
                laPosition = mCustomer.position.loc[1];
                loPosition = mCustomer.position.loc[0];
                addres = mCustomer.position.addr;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        tv_customer_name.setText(mCustomer.name);

        if (null != mCustomer && null != mCustomer.position && !TextUtils.isEmpty(mCustomer.position.addr)) {
            tv_address.setText(mCustomer.position.addr);
        } else {
            Intent intent = new Intent();
            Bundle bundle = intent.getExtras();
            try {
                tv_address.setText(bundle.getString("CustomerAddress"));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(mCustomer.loc.addr)) {
            edt_address_details.setText(mCustomer.loc.addr);
        }

        try {
            tv_customer_creator.setText(mCustomer.creator.getName());
        } catch (NullPointerException e) {
            tv_customer_creator.setText("无");
            e.printStackTrace();
        }

        String responser = (null == mCustomer.owner || null == mCustomer.owner) ? "" : mCustomer.owner.name;
        tv_customer_responser.setText(responser);
        if (members.size() != 0) {
            if (isMyUser && !isMenber) {
                img_del_join_users.setVisibility(View.VISIBLE);//删除参与人按钮
            }
            tv_customer_join_users.setText(Utils.getMembers(members));
        } else {
            tv_customer_join_users.setText("无参与人");
        }
        if (regional.province != null) {
            tv_district.setText(regional.province + " " + regional.city + " " + regional.county + " ");
        }
        edt_customer_memo.setText(mCustomer.summary);
        tv_customer_create_at.setText(app.df3.format(new Date(mCustomer.createdAt * 1000)));

        if (mCustomer.tags != null && mCustomer.tags.size() > 0) {
            mTagItems = mCustomer.tags;
            setTag();
        }
    }

    @UiThread
    void setTag() {
        String tag = appendTagItem(mTagItems);
        if (!TextUtils.isEmpty(tag)) {
            tv_labels.setText(tag);
        }
    }

    void setEnable() {
        isMem = true;
        layout_rushpackger.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
        img_refresh_address.setVisibility(View.GONE);
        img_title_right.setVisibility(View.GONE);
        tv_customer_name.setEnabled(false);
        tv_address.setEnabled(false);
        edt_customer_memo.setEnabled(false);
        layout_customer_district.setEnabled(false);
        layout_customer_responser.setEnabled(false);
        layout_customer_join_users.setEnabled(false);
        img_refresh_address.setEnabled(false);

        container.setClickable(false);
        container.setEnabled(false);
        tv_address.setTextColor(getResources().getColor(R.color.md_grey_500));
        tv_district.setTextColor(getResources().getColor(R.color.md_grey_500));
        tv_customer_responser.setTextColor(getResources().getColor(R.color.md_grey_500));
        tv_customer_join_users.setTextColor(getResources().getColor(R.color.md_grey_500));
    }

    /**
     * 显示修改负责任 对话框
     */
    private void showLeaveDialog() {
        showGeneralDialog(true, true, getString(R.string.app_userdetalis_message));
        //确定
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                SelectDetUserActivity2.startThisForOnly(CustomerInfoActivity.this, null);

            }
        });
        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
            }
        });
    }

    /**
     * 验证必填动态字段是否填写
     */

    private boolean testDynamicword() {
        for (ExtraData extDatas : mCustomer.extDatas) {
            if (extDatas.getProperties().isRequired() && extDatas.getProperties().isEnabled()) {
                LogUtil.d("动态字段必填:" + extDatas.getProperties().isRequired());
                if (extDatas.getVal().isEmpty() || null == extDatas.getVal()) {
                    return false;
                }
            }
        }
        return true;
    }


    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_label,
            R.id.img_refresh_address, R.id.img_go_where, R.id.img_del_join_users,
            R.id.layout_customer_responser, R.id.layout_customer_join_users, R.id.layout_customer_district})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:

                if (!testDynamicword()) {
                    Toast("请填写必填选项");
                } else {
                    updateCustomer();
                }

                break;

            case R.id.layout_customer_label:
                Bundle bundle2 = new Bundle();
                if (mTagItems != null) {
                    bundle2.putSerializable("tagitems", Utils.convertTagItems(mTagItems));
                    bundle2.putString("customerId", mCustomer.getId());
                    bundle2.putBoolean("isMem", isMem);
                }
                app.startActivityForResult(this, CustomerLabelActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_LABEL, bundle2);
                break;
            /*刷新地理位置*/
            case R.id.img_refresh_address:
                mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_DETAILS_PAGE);
                if (null != mCustomer.position && mCustomer.position.loc.length > 0) {
                    mBundle.putDoubleArray("loc", mCustomer.position.loc);
                    mBundle.putString("address", mCustomer.position.addr);
                }
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                break;
            /*路径规划*/
            case R.id.img_go_where:
                Utils.goWhere(this, laPosition, loPosition, addres);
                break;
            /*清除参与人*/
            case R.id.img_del_join_users:
                mManagerIds = null;
                members.clear();
                tv_customer_join_users.setText("");
                img_del_join_users.setVisibility(View.GONE);
                break;
            /*选负责人*/
            case R.id.layout_customer_responser:
                showLeaveDialog();
                break;
            /*选参与人*/
            case R.id.layout_customer_join_users:
                SelectDetUserActivity2.startThisForMulitSelect(CustomerInfoActivity.this, mManagerIds == null ? null : mManagerIds.toString(), false);
                break;
            /*地区选择*/
            case R.id.layout_customer_district:
                loadAreaCodeTable();
                break;
            default:
                break;
        }
    }

    /**
     * 更新客戶
     */
    private void updateCustomer() {
        Locate adrDetailsData = new Locate();
        String customerName = tv_customer_name.getText().toString().trim();
        String customerAddress = tv_address.getText().toString().trim();
        String summary = edt_customer_memo.getText().toString().trim();
        String addressDetails = edt_address_details.getText().toString().trim();

        if (TextUtils.isEmpty(customerName)) {
            Toast("客户姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(customerAddress) && cusLocation) {
            Toast("客户地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(addressDetails) && cusDetialAdress) {
            Toast("客户详细地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(summary) && cusBrief) {
            Toast("客户简介不能为空");
            return;
        }
        if (!TextUtils.isEmpty(addressDetails)) {
            adrDetailsData.addr = addressDetails;
        }

        showLoading("");
        mLocate.addr = customerAddress;
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", customerName);
        map.put("summary", summary);
        map.put("owner", owner);
        map.put("members", members);
        map.put("tags", mTagItems);
        map.put("loc", adrDetailsData);
        map.put("position", mLocate);
        map.put("extDatas", mCustomer.extDatas);
        map.put("regional", regional);

        LogUtil.d("提交客户信息，发送的数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                updateCustomer(mCustomer.getId(), map, new RCallback<Customer>() {
                    @Override
                    public void success(final Customer customer, final Response response) {
                        HttpErrorCheck.checkResponse("更新客户信息", response);
                        app.isCutomerEdit = true;
                        Intent intent = new Intent();
                        customer.loc = mLocate;
                        boolean isCreator;
                        if (!owner.id.equals(MainApp.user.getId())) {
                            isCreator = false;
                        } else {
                            isCreator = true;
                        }
                        intent.putExtra("isCreator", isCreator);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, CustomerManagerActivity.CUSTOMER_COMM_RUSH, intent);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Customer.class.getName(), mCustomer);
        intent.putExtra("isCreator", true);//默认为true
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        switch (requestCode) {

            /**
             * 地图微调，数据回调
             * */
            case MapModifyView.SERACH_MAP:

                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    tv_address.setText(positionResultItem.address);
                    mLocate.addr = positionResultItem.address;
                    mLocate.setLoc(new double[]{loPosition, laPosition});
                }

                break;

            /**
             * 负责人回调
             * */
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser nu = (NewUser) data.getSerializableExtra("data");
                owner.id = nu.getId();
                owner.name = nu.getName();
                owner.avatar = nu.getAvatar();
                tv_customer_responser.setText(nu.getName());
                break;

            /**
             * 参与人回调
             * */
            case SelectDetUserActivity2.REQUEST_MULTI_SELECT:

                cusMembers = (Members) data.getSerializableExtra("data");
                mManagerNames = new StringBuffer();
                mManagerIds = new StringBuffer();

                if (members != null) {
                    if (null != cusMembers.depts) {
                        for (com.loyo.oa.v2.beans.NewUser newUser : cusMembers.depts) {
                            if (!MainApp.user.id.equals(newUser.getId())) {
                                mManagerNames.append(newUser.getName() + ",");
                                mManagerIds.append(newUser.getId() + ",");
                            } else {
                                Toast("你已经是负责人，不能选自己为参与人!");
                            }
                        }
                    }
                    if (null != cusMembers.users) {
                        for (com.loyo.oa.v2.beans.NewUser newUser : cusMembers.users) {
                            if (!MainApp.user.id.equals(newUser.getId())) {
                                mManagerNames.append(newUser.getName() + ",");
                                mManagerIds.append(newUser.getId() + ",");
                            } else {
                                Toast("你已经是负责人，不能选自己为参与人!");
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(mManagerNames)) {
                        mManagerNames.deleteCharAt(mManagerNames.length() - 1);
                    }
                }

                members = Utils.convert2Members(mManagerIds.toString(), mManagerNames.toString());
                if (members.size() != 0) {
                    img_del_join_users.setVisibility(View.VISIBLE);
                    tv_customer_join_users.setText(mManagerNames);
                } else {
                    tv_customer_join_users.setText("无参与人");
                }

                break;

            case REQUEST_CUSTOMER_EDIT_BASEINFO:
                mCustomer = (Customer) data.getSerializableExtra("customer");
                tv_customer_name.setText(mCustomer.name);
                break;
            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                mTagItems = (ArrayList<NewTag>) bundle.getSerializable("data");
                tv_labels.setText(appendTagItem(mTagItems));
                mCustomer.tags = mTagItems;
                break;
            default:

                break;
        }
    }

    /**
     * 拼接mTagItems.getItemName, 如果mTagitems为空，返回“”；
     *
     * @param tagList
     * @return
     */
    private String appendTagItem(final List<NewTag> tagList) {
        StringBuffer sb = null;

        for (NewTag item : tagList) {
            if (sb == null) {
                sb = new StringBuffer();
                sb.append(String.valueOf(item.getItemName()));
            } else {
                sb.append("/");
                sb.append(String.valueOf(item.getItemName()));
            }
        }

        return null == sb ? "" : sb.toString();
    }

}