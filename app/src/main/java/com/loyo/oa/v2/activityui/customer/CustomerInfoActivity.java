package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerExtraData;
import com.loyo.oa.v2.activityui.customer.model.CustomerRegional;
import com.loyo.oa.v2.activityui.customer.model.CustomerStatusModel;
import com.loyo.oa.v2.activityui.customer.model.ExtraData;
import com.loyo.oa.v2.activityui.customer.model.Locate;
import com.loyo.oa.v2.activityui.customer.model.Member;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.CustomerInfoExtraData;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【客户信息】 页面
 */
@EActivity(R.layout.activity_customer_info)
public class CustomerInfoActivity extends BaseFragmentActivity {

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_UPDATE_CONTRACT = 7;
    public static final int REQUEST_CUSTOMER_EDIT_BASEINFO = 9;
    private static final int REQUEST_ACTIVITY_CODE_STATUS = 10;


    @ViewById
    ViewGroup img_title_left, layout_Extra, img_title_right, layout_customer_district, layout_customer_label,
            layout_customer_responser, layout_customer_join_users, ll_common, layout_customer_status;
    @ViewById(R.id.layout_customer_optional_info)
    LinearLayout containerOp, layout_rushpackger;
    @ViewById
    EditText tv_address, tv_customer_name, edt_customer_memo, edt_address_details, edt_customer_weburl;
    @ViewById
    TextView tv_customer_creator, tv_customer_responser, tv_customer_join_users, tv_customer_create_at,
            tv_labels, tv_district, tv_common_persion, tv_common_type, tv_common_reason, tv_customer_recycledAt;
    @ViewById
    ImageView img_go_where, img_refresh_address, img_del_join_users;
    @ViewById
    LoadingLayout ll_loading;

    @ViewById
    TextView tv_common_lose, tv_customer_status;

    Customer mCustomer;
    @Extra("CustomerId")
    String mCustomerId;
    @Extra("canEdit")
    boolean canEdit;
    private Members cusMembers = new Members();
    private StringBuffer mManagerIds = new StringBuffer();
    private StringBuffer mManagerNames = new StringBuffer();
    private ArrayList<CustomerExtraData> mCustomerExtraDatas;
    private PositionResultItem positionResultItem;//地图，回调的地址
    private LinearLayout containerRe;

    private double laPosition;//当前位置的经纬度
    private double loPosition;
    private boolean cusLocation = false;//定位权限
    private boolean cusDetialAdress = false;//客户的详细地址
    private boolean cusBrief = false;//客户简介

    private ArrayList<ExtraData> extDatas; //动态数据集
    private ArrayList<ExtraData> opextDatasModel = new ArrayList<>(); //非必填动态数据
    private ArrayList<ExtraData> reextDatasModel = new ArrayList<>(); //必填动态数据

    private String beforeOwnerId = "";//修改之前，负责人的id

    @AfterViews
    void initUI() {
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getCustomer();
            }
        });
        containerRe = (LinearLayout) findViewById(R.id.layout_customer_required_info);
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
        Global.SetTouchView(layout_customer_status);
        ((TextView) findViewById(R.id.tv_title_1)).setText("客户信息");
        img_title_right.setEnabled(false);//禁用，加载完成数据以后，才可以点击
        getCustomer();
    }

    /**
     * 获取客户动态字段
     */
    void getExtraData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 100);
        CustomerService.getCustomerDynamic(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<CustomerExtraData>>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<CustomerExtraData> customerExtraDataArrayList) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        mCustomerExtraDatas = customerExtraDataArrayList;
                        initData();
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
        CustomerService.getAddCustomerJur(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>() {
                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        for (ContactLeftExtras customerJur : contactLeftExtrasArrayList) {
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
                    public void onError(Throwable e) {
                    }
                });
    }

    /**
     * 获取用户信息
     */
    void getCustomer() {
        CustomerService.getCustomerDetailById(mCustomerId)
                .subscribe(new DefaultLoyoSubscriber<Customer>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Customer customer) {
                        mCustomer = customer;
                        beforeOwnerId = mCustomer.owner.id;
                        getExtraData();
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
                if (null == mCustomer.regional) {
                    mCustomer.regional = new CustomerRegional();
                }
                mCustomer.regional.province = cityArr[0];
                mCustomer.regional.city = cityArr[1];
                mCustomer.regional.county = cityArr[2];
                selectCityView.dismiss();
            }
        });
    }

    /**
     * 初始化动态字段
     */
    private void initExtra(final boolean editable) {
        if (null == mCustomer.extDatas) return;
        extDatas = new ArrayList<>();
        extDatas.addAll(mCustomer.extDatas);
        /*动态字段 数据转换*/
        if (null != extDatas && !extDatas.isEmpty()) {
            for (ExtraData extraData : extDatas) {
                for (CustomerExtraData customerExtraData : mCustomerExtraDatas) {
                    if (extraData.getProperties().getName().equals(customerExtraData.getName())) {
                        extraData.getProperties().setEnabled(customerExtraData.isEnabled());
                        extraData.getProperties().setRequired(customerExtraData.isRequired());
                        extraData.getProperties().setLabel(customerExtraData.getLabel());
                    }
                }
            }

            /*分离必填与非必填字段*/
            for (ExtraData ext : extDatas) {
                if (ext.getProperties().isRequired()) {
                    reextDatasModel.add(ext);
                } else {
                    opextDatasModel.add(ext);
                }
            }

            containerOp.setVisibility(View.VISIBLE);
            containerOp.addView(new CustomerInfoExtraData(mContext, opextDatasModel, editable, R.color.title_bg1, 0));
            containerRe.setVisibility(View.VISIBLE);
            containerRe.addView(new CustomerInfoExtraData(mContext, reextDatasModel, editable, R.color.title_bg1, 0));

        }
    }

    void initData() {
        if (mCustomer == null) {
            return;
        }

        this.canEdit = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.EDIT);
        boolean canChangeResponser = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.RESPONSIBLE_PERSON_CHANGE);
        boolean canChangeMember = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.PARTICIPATED_PERSON_CHANGE);

        img_title_right.setEnabled(true);//数据加载完了，可以提交
        updateUiWithEditAuth(canEdit);
        updateUiWithResponserAuth(canChangeResponser);
        updateUiWithMemberAuth(canChangeMember);
        initExtra(canEdit);

        //客户名字
        tv_customer_name.setText(mCustomer.name);
        //客户介绍
        edt_customer_memo.setText(mCustomer.summary);
        //创建时间
        tv_customer_create_at.setText(DateTool.getDateTimeFriendly(mCustomer.createdAt));
        //网址
        edt_customer_weburl.setText(mCustomer.webSite);
        //客户状态
        tv_customer_status.setText(mCustomer.statusName);
        //客户详细地址
        edt_address_details.setText(mCustomer.loc.addr);

        if (null != mCustomer.position) {
            if (null != mCustomer.position.loc && mCustomer.position.loc.length > 1) {
                laPosition = mCustomer.position.loc[1];
                loPosition = mCustomer.position.loc[0];
            }
            if (!TextUtils.isEmpty(mCustomer.position.addr)) {
                tv_address.setText(mCustomer.position.addr);
            }
        } else {
            mCustomer.position = new Locate();
        }
        if (null != mCustomer.creator && !TextUtils.isEmpty(mCustomer.creator.getName())) {
            tv_customer_creator.setText(mCustomer.creator.getName());
        } else {
            tv_customer_creator.setText("无");
        }


        String responser = (null == mCustomer.owner || null == mCustomer.owner) ? "" : mCustomer.owner.name;
        tv_customer_responser.setText(responser);
        if (mCustomer.members.size() != 0) {
            if (canChangeMember) {
                img_del_join_users.setVisibility(View.VISIBLE);//删除参与人按钮
            }
            tv_customer_join_users.setText(Utils.getMembers(mCustomer.members));
        } else {
            tv_customer_join_users.setText("无参与人");
        }
        if (null != mCustomer.regional) {
            tv_district.setText(mCustomer.regional.province + " " + mCustomer.regional.city + " " + mCustomer.regional.county + " ");

        }

        //设置标签
        if (mCustomer.tags != null && mCustomer.tags.size() > 0) {
            setTag();
        }


        /* 公海客户特殊操作 */
        if (mCustomer.state == Customer.DumpedCustomer) {
            layout_Extra.setVisibility(View.GONE);
            ll_common.setVisibility(View.VISIBLE);
            tv_customer_recycledAt.setText(mCustomer.recycledAt != 0 ? DateTool.getDateTimeFriendly(mCustomer.recycledAt) : "--");
            tv_common_persion.setText(mCustomer.owner == null ? "--" : mCustomer.owner.name);
            tv_common_type.setText(mCustomer.recycleType.getText());
            tv_common_reason.setText(TextUtils.isEmpty(mCustomer.recycleReason) ? "--" : mCustomer.recycleReason);
            if (mCustomer.recycleReason.length() > 15)
                tv_common_reason.setGravity(Gravity.LEFT | Gravity.CENTER);
            tv_common_lose.setText(mCustomer.getRecycleName());
        } else {
            layout_Extra.setVisibility(View.VISIBLE);
            ll_common.setVisibility(View.GONE);
        }
    }

    @UiThread
    void setTag() {
        String tag = appendTagItem(mCustomer.tags);
        if (!TextUtils.isEmpty(tag)) {
            tv_labels.setText(tag);
        }
    }

    void updateUiWithEditAuth(boolean canEdit) {
        img_refresh_address.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        img_title_right.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        layout_rushpackger.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        tv_customer_name.setEnabled(canEdit);
        tv_address.setEnabled(canEdit);
        edt_customer_memo.setEnabled(canEdit);
        layout_customer_district.setEnabled(canEdit);
        img_refresh_address.setEnabled(canEdit);
        layout_customer_label.setEnabled(canEdit);
        layout_customer_status.setEnabled(canEdit);
        edt_customer_weburl.setEnabled(canEdit);
        containerOp.setClickable(canEdit);
        containerOp.setEnabled(canEdit);
        edt_address_details.setEnabled(canEdit);
        if (canEdit) {
            tv_customer_status.setTextColor(getResources().getColor(R.color.title_bg1));
            tv_address.setTextColor(getResources().getColor(R.color.text33));
            edt_customer_memo.setTextColor(getResources().getColor(R.color.text66));
            tv_district.setTextColor(getResources().getColor(R.color.title_bg1));
            tv_labels.setTextColor(getResources().getColor(R.color.title_bg1));
            edt_customer_weburl.setTextColor(getResources().getColor(R.color.text66));
            tv_district.setTextColor(getResources().getColor(R.color.text66));
        } else {
            tv_customer_status.setTextColor(getResources().getColor(R.color.md_grey_500));
            tv_address.setTextColor(getResources().getColor(R.color.md_grey_500));
            tv_district.setTextColor(getResources().getColor(R.color.md_grey_500));
            tv_labels.setTextColor(getResources().getColor(R.color.md_grey_500));
            edt_customer_weburl.setTextColor(getResources().getColor(R.color.md_grey_500));
            edt_customer_memo.setTextColor(getResources().getColor(R.color.md_grey_500));
            edt_customer_memo.setHint("");
            findViewById(R.id.iv_district).setVisibility(View.GONE);
            findViewById(R.id.iv_labels).setVisibility(View.GONE);
            findViewById(R.id.iv_customer_status).setVisibility(View.GONE);
            tv_labels.setPadding(tv_labels.getPaddingLeft(), tv_labels.getPaddingTop(), DensityUtil.dp2px(mContext, 14), tv_labels.getPaddingBottom());
            tv_customer_status.setPadding(tv_labels.getPaddingLeft(), tv_labels.getPaddingTop(), DensityUtil.dp2px(mContext, 14), tv_labels.getPaddingBottom());
            edt_customer_weburl.setPadding(tv_labels.getPaddingLeft(), tv_labels.getPaddingTop(), DensityUtil.dp2px(mContext, 14), tv_labels.getPaddingBottom());

        }

    }

    void updateUiWithResponserAuth(boolean canChangeResponser) {
        layout_customer_responser.setEnabled(canChangeResponser);
        if (canChangeResponser) {
            tv_customer_responser.setTextColor(getResources().getColor(R.color.title_bg1));
        } else {
            tv_customer_responser.setTextColor(getResources().getColor(R.color.md_grey_500));
        }

    }

    void updateUiWithMemberAuth(boolean canChangeMember) {
        layout_customer_join_users.setEnabled(canChangeMember);
        if (canChangeMember) {
            tv_customer_join_users.setTextColor(getResources().getColor(R.color.title_bg1));
        } else {
            tv_customer_join_users.setTextColor(getResources().getColor(R.color.md_grey_500));
        }

    }

    /**
     * 显示修改负责任 对话框
     */
    private void showLeaveDialog() {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
        if (mCustomer.owner != null) {
            OrganizationalMember ownerUser = new OrganizationalMember();
            ownerUser.setAvatar(mCustomer.owner.getAvatar());
            ownerUser.setId(mCustomer.owner.getId());
            ownerUser.setName(mCustomer.owner.getName());
            StaffMemberCollection collection = Compat.convertNewUserToStaffCollection(ownerUser);
            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
        }
        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
        Intent intent = new Intent();
        intent.setClass(CustomerInfoActivity.this, ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 验证必填动态字段是否填写
     */

    private boolean testDynamicword() {
        if (extDatas == null) {
            extDatas = new ArrayList<>();
        }
        extDatas.clear();
        extDatas.addAll(opextDatasModel);
        extDatas.addAll(reextDatasModel);

        if (null != extDatas) {
            for (ExtraData ext : extDatas) {
                if (ext.getProperties().isRequired() && ext.getProperties().isEnabled()) {
                    LogUtil.d("动态字段必填:" + ext.getProperties().isRequired());
                    if (ext.getVal().isEmpty() || null == ext.getVal()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_label,
            R.id.img_refresh_address, R.id.img_go_where, R.id.img_del_join_users,
            R.id.layout_customer_responser, R.id.layout_customer_join_users, R.id.layout_customer_district,
            R.id.layout_customer_status})
    void onClick(final View v) {
        Intent mIntent;
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
            //客户状态
            case R.id.layout_customer_status:

                Bundle b = new Bundle();
                b.putString(CustomerStatusSingleSelectActivity.EXTRA_CURRENT, mCustomer.statusId);//设置默认值
                app.startActivityForResult(this, CustomerStatusSingleSelectActivity.class, app.ENTER_TYPE_RIGHT, REQUEST_ACTIVITY_CODE_STATUS, b);

                break;
            //客户标签
            case R.id.layout_customer_label:
                mIntent = new Intent(CustomerInfoActivity.this, CustomerLabelCopyActivity.class);
                mIntent.putExtra("canEdit", canEdit);
                mIntent.putExtra("fromPage", 1);
                if (null != mCustomer.tags) {
                    mIntent.putExtra("tagitems", Utils.convertTagItems(mCustomer.tags));
                    mIntent.putExtra("customerId", mCustomer.getId());
                }
                startActivity(mIntent);
                break;

            /*刷新地理位置*/
            case R.id.img_refresh_address:
                Bundle mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_DETAILS_PAGE);
                if (null != mCustomer.position && mCustomer.position.validatedLocation()) {
                    mBundle.putDoubleArray("loc", mCustomer.position.loc);
                    mBundle.putString("address", mCustomer.position.addr);
                }
                app.startActivityForResult(this, MapModifyView.class, MainApp.ENTER_TYPE_RIGHT, MapModifyView.SERACH_MAP, mBundle);
                break;
            /*路径规划*/
            case R.id.img_go_where:
                Utils.goWhere(this, laPosition, loPosition, mCustomer.position.addr);
                break;
            /*清除参与人*/
            case R.id.img_del_join_users:
                mManagerIds = null;
                mCustomer.members.clear();
                tv_customer_join_users.setText("");
                img_del_join_users.setVisibility(View.GONE);
                break;
            /*选负责人*/
            case R.id.layout_customer_responser:
                showLeaveDialog();
                break;
            /*选参与人*/
            case R.id.layout_customer_join_users: {
                Members selectedMembers = new Members();
                for (Member m : mCustomer.members) {
                    selectedMembers.users.add(m.getUser());
                }

                StaffMemberCollection collection = Compat.convertMembersToStaffCollection(selectedMembers);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
                bundle.putBoolean(ContactPickerActivity.DEPARTMENT_SELECTION_KEY, false);
                if (collection != null) {
                    bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
                }
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_INVOLVE_USER_REQUEST);
                Intent intent = new Intent();
                intent.setClass(this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
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
        final Locate adrDetailsData = new Locate();
        mCustomer.name = tv_customer_name.getText().toString().trim();
        mCustomer.summary = edt_customer_memo.getText().toString().trim();
        String addressDetails = edt_address_details.getText().toString().trim();

        if (TextUtils.isEmpty(mCustomer.name)) {
            Toast("客户姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(mCustomer.position.addr) && cusLocation) {
            Toast("客户地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(addressDetails) && cusDetialAdress) {
            Toast("客户详细地址不能为空");
            return;
        }
        if (TextUtils.isEmpty(mCustomer.summary) && cusBrief) {
            Toast("客户简介不能为空");
            return;
        }
        if (!TextUtils.isEmpty(addressDetails)) {
            adrDetailsData.addr = addressDetails;
        }

        final HashMap<String, Object> map = new HashMap<>();
        map.put("name", mCustomer.name);
        map.put("summary", mCustomer.summary);
        map.put("owner", mCustomer.owner);
        map.put("members", mCustomer.members);
        map.put("tags", mCustomer.tags);
        map.put("loc", adrDetailsData);
        map.put("position", mCustomer.position);
        map.put("extDatas", extDatas);
        map.put("regional", mCustomer.regional);
        map.put("statusId", mCustomer.statusId);//状态
        String url = edt_customer_weburl.getText() + "";
        //输入的有内容，才提交
        if (url.length() > 8) {
            map.put("webSite", url);
        }

        showCommitLoading();
        CustomerService.updateCustomer(mCustomer.getId(), map)
                .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                    @Override
                    public void onNext(final Customer customer) {
                        //权限 要用最新的，这里只覆盖可能变化的属性，不用customer替代，主要是怕服务端没有完全返回数据，主要是更新前面的客户详情的数据
                        mCustomer.state = customer.state;
                        mCustomer.relationState = customer.relationState;
                        MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent(mCustomer);
                        myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_UPDATE;
                        myCustomerRushEvent.subCode = MyCustomerRushEvent.EVENT_SUB_CODE_INFO;
                        myCustomerRushEvent.session = mCustomer.getId();
                        AppBus.getInstance().post(myCustomerRushEvent);
                        //如果修改了客户负责人,负责人不是自己，那就要从客户列表中删除
                        if (!beforeOwnerId.equals(mCustomer.owner.id)) {
                            MyCustomerRushEvent delListViewItemEvent = new MyCustomerRushEvent(mCustomer);
                            delListViewItemEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_DEL;
                            delListViewItemEvent.session = mCustomer.getId();
                            AppBus.getInstance().post(delListViewItemEvent);
                        }
                        finish();
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


    /**
     * 更新Label
     *
     * @param event
     */
    @Subscribe
    public void onCustomerRushEvent(MyCustomerRushEvent event) {
        if (MyCustomerRushEvent.EVENT_CODE_UPDATE == event.eventCode && event.subCode == MyCustomerRushEvent.EVENT_SUB_CODE_LABEL) {
            mCustomer.tags = event.data.tags;
            setTag();
        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
            if (user == null) {
                return;
            } else {
                mCustomer.owner.id = user.getId();
                mCustomer.owner.name = user.getName();
                mCustomer.owner.avatar = user.getAvatar();
                tv_customer_responser.setText(user.getName());
            }
        } else if (FinalVariables.PICK_INVOLVE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            Members selectedMembers = Compat.convertStaffCollectionToMembers(collection);
            if (selectedMembers == null) {
                return;
            }
            cusMembers = selectedMembers;
            mManagerNames = new StringBuffer();
            mManagerIds = new StringBuffer();
            /*判断当前负责人用owner对象*/
            if (mCustomer.members != null) {
                if (cusMembers.depts.size() > 0) {
                    for (OrganizationalMember newUser : cusMembers.depts) {
                        if (!mCustomer.owner.id.equals(newUser.getId())) {
                            mManagerNames.append(newUser.getName() + ",");
                            mManagerIds.append(newUser.getId() + ",");
                        } else {
                            Toast("你已经是负责人，不能选自己为参与人!");
                        }
                    }
                }
                if (cusMembers.users.size() > 0) {
                    for (OrganizationalMember newUser : cusMembers.users) {
                        if (!mCustomer.owner.id.equals(newUser.getId())) {
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
            mCustomer.members = Utils.convert2Members(mManagerIds.toString(), mManagerNames.toString());
            if (mCustomer.members.size() != 0) {
                img_del_join_users.setVisibility(View.VISIBLE);
                tv_customer_join_users.setText(mManagerNames);
            } else {
                tv_customer_join_users.setText("无参与人");
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        switch (requestCode) {

            //客户状态
            case REQUEST_ACTIVITY_CODE_STATUS:
                CustomerStatusModel.CustomerStatusItemModel itemModel = (CustomerStatusModel.CustomerStatusItemModel) data.getSerializableExtra("data");
                mCustomer.statusId = itemModel.id;
                mCustomer.statusName = itemModel.name;
                tv_customer_status.setText(itemModel.name);
                break;
            /**
             * 地图微调，数据回调
             * */
            case MapModifyView.SERACH_MAP:

                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if (null != positionResultItem) {
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    tv_address.setText(positionResultItem.address);
                    mCustomer.position.addr = positionResultItem.address;
                    mCustomer.position.setLoc(new double[]{loPosition, laPosition});
                }

                break;

            /**
             * 负责人回调
             * */
            case FinalVariables.REQUEST_ONLY:
                OrganizationalMember nu = (OrganizationalMember) data.getSerializableExtra("data");
                mCustomer.owner.id = nu.getId();
                mCustomer.owner.name = nu.getName();
                mCustomer.owner.avatar = nu.getAvatar();
                tv_customer_responser.setText(nu.getName());
                break;

            /**
             * 参与人回调
             * */
            case FinalVariables.REQUEST_MULTI_SELECT:

                cusMembers = (Members) data.getSerializableExtra("data");
                mManagerNames = new StringBuffer();
                mManagerIds = new StringBuffer();

                if (mCustomer.members != null) {
                    if (null != cusMembers.depts) {
                        for (OrganizationalMember newUser : cusMembers.depts) {
                            if (!MainApp.user.id.equals(newUser.getId())) {
                                mManagerNames.append(newUser.getName() + ",");
                                mManagerIds.append(newUser.getId() + ",");
                            } else {
                                Toast("你已经是负责人，不能选自己为参与人!");
                            }
                        }
                    }
                    if (null != cusMembers.users) {
                        for (OrganizationalMember newUser : cusMembers.users) {
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

                mCustomer.members = Utils.convert2Members(mManagerIds.toString(), mManagerNames.toString());
                if (mCustomer.members.size() != 0) {
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
                mCustomer.tags = (ArrayList<NewTag>) bundle.getSerializable("data");
                tv_labels.setText(appendTagItem(mCustomer.tags));
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