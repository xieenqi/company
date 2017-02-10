package com.loyo.oa.v2.activityui.customer.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.UMengTools;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 【团队客户】列表
 * Created by yyy on 16/6/1.
 */
public class TeamCustomerFragment extends BaseCustomerFragment {
    @Override
    protected void initFilterParams() {
        params.put("field","lastActAt");
        params.put("order","asc");
    }

    @Override
    protected void success(PaginationX<Customer> customerPaginationX) {
        super.success(customerPaginationX);
    }

    @Override
    protected void fail(Throwable e) {
        super.fail(e);
    }

    @Override
    protected void loadFilterOptions() {
        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(BusinessOperation.CUSTOMER_MANAGEMENT)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.CUSTOMER_MANAGEMENT)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        } else {
            title = "我";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(new OrganizationFilterModel(depts, title));
        options.add(TimeFilterModel.getFilterModel2());
        options.add(TagMenuModel.getTagFilterModel(mTags));
        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                if (menuIndex == 0) {
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        params.put("deptId",model.getKey());
                        params.put("userId","");
                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        params.put("deptId","");
                        params.put("userId",model.getKey());
                    }
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.departmentCustomerTeam);
                } else if (menuIndex == 1) { // TimeFilterModel
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    String[] keys = key.split(" ");
                    params.put("field",keys[0]);
                    params.put("order",keys[1]);
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.timeCustomerTeam);
                } else if (menuIndex == 2) { // TagFilter
                    params.put("tagsParams",userInfo.toString());
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.tagCustomerTeam);
                }
                mPagination.setFirstPage();
                getData();
            }
        });
    }


    @Override
    protected void onNearCustomerBtn() {
        Bundle bundle = new Bundle();
        bundle.putString("position", position);
        bundle.putSerializable("nearCount", nearCount);
        bundle.putInt("type", CustomerManagerActivity.NEARCUS_TEAM);//团队2 个人1
        app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_ZOOM_IN, false, bundle);
        UmengAnalytics.umengSend(mActivity, UmengAnalytics.customerNearby);
    }


    /**
     * 获取数据
     */
    protected void getData() {
        params.put("pageIndex", mPagination.getShouldLoadPageIndex());
        params.put("pageSize", mPagination.getPageSize());

        CustomerService.getTeamCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        fail(e);
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
                       success(customerPaginationX);
                        getNearCustomersInfo();
                    }
                });
    }


    /**
     * http获取附近客户信息
     */
    protected void getNearCustomersInfo() {
        new LocationUtilGD(mActivity, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {
                LocationUtilGD.sotpLocation();
                position = String.valueOf(longitude).concat(",").concat(String.valueOf(latitude));
                CustomerService.getNearbyTeamCustomerCount(position)
                        .subscribe(new DefaultLoyoSubscriber<NearCount>() {
                            public void onNext(NearCount count) {
                                nearCount = count;
                                if (null != nearCount) {
                                    nearTv.setText("发现" + nearCount.total + "个附近客户");
                                    showNearCustomersView();
                                }
                            }
                        });
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                LocationUtilGD.sotpLocation();
            }
        });
    }

    /**
     * 刷新列表回调
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerRushEvent event) {
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            //客户详情操作回调
            case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                getData();
                break;
        }
    }
}
