package com.loyo.oa.v2.activityui.customer.fragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.TagMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.TimeFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.NearByCustomersActivity_;
import com.loyo.oa.v2.activityui.customer.adapter.TeamCustomerAdapter;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerListRushEvent;
import com.loyo.oa.v2.activityui.customer.model.CustomerTageConfig;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UMengTools;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【团队客户】列表
 * Created by yyy on 16/6/1.
 */
public class TeamCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private TextView nearTv;
    private ViewGroup nearLayout;
    private NearCount nearCount;
    private PullToRefreshListView listView;
    private TeamCustomerAdapter adapter;
    private DropDownMenu filterMenu;

    private ArrayList<Tag> mTags;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private String field = "lastActAt";
    private String order = "desc";
    private String userId = "";
    private String tagsParams = "";
    private String departmentId = "";
    private String position;
    private int page = 1;
    private boolean isPullUp = false;
    private LoadingLayout ll_loading;

    @Override
    public void onResume() {
        super.onResume();
        getRefershData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_cus, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        isPullUp = false;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        isPullUp = true;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    public void initView(View view) {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                getRefershData();
            }
        });
//        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        mTags = CustomerTageConfig.getTageCache();
        nearTv = (TextView) view.findViewById(R.id.tv_near_customers);
        nearLayout = (ViewGroup) view.findViewById(R.id.layout_near_customers);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        nearLayout.setOnClickListener(click);
        nearLayout.setOnTouchListener(Global.GetTouch());

        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
    }

    private void loadFilterOptions() {

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
                    // TODO:
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        departmentId = model.getKey();
                        userId = "";
                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        departmentId = "";
                        userId = model.getKey();
                    }
                } else if (menuIndex == 1) { // TimeFilterModel
                    MenuModel model = selectedModels.get(0);
                    String key = model.getKey();
                    String value = model.getValue();
                    filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                    String[] keys = key.split(" ");
                    field = keys[0];
                    order = keys[1];
                } else if (menuIndex == 2) { // TagFilter
                    tagsParams = userInfo.toString();
                }
                getRefershData();
            }
        });
    }


    /**
     * 显示附近客户
     */

    private void showNearCustomersView() {
        nearLayout.setVisibility(View.VISIBLE);
        int oX = app.diptoPx(240);
        int nX = 0;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(nearLayout, "translationX", oX, nX);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.setTarget(nearLayout);
        objectAnimator.start();
    }


    /**
     * http获取附近客户信息
     */
    private void getNearCustomersInfo() {
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
//                Toast("定位失败！");
            }
        });
    }

    private void getRefershData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        page = 1;
        isPullUp = false;
        getData();
    }

    /**
     * 获取数据,默认设置倒序
     */
    private void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 15);
        params.put("field", field);
        params.put("order", order);
        params.put("tagsParams", tagsParams);
        params.put("deptId", departmentId);
        params.put("userId", userId);
        LogUtil.d("客户查询传递参数：" + MainApp.gson.toJson(params));

        CustomerService.getTeamCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                        @LoyoErrorChecker.CheckType
                        int type = mCustomers.size() > 0 ?
                                LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        listView.onRefreshComplete();
                    }

                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                            } else {
                                Toast("没有更多数据了");
                                listView.onRefreshComplete();
                                return;
                            }
                        } else {
                            mPagination = customerPaginationX;
                            if (!isPullUp) {
                                mCustomers.clear();
                            }
                            mCustomers.addAll(customerPaginationX.getRecords());
                            bindData();
                        }
                        getNearCustomersInfo();
                        listView.onRefreshComplete();
                        MainApp.getMainApp().isCutomerEdit = false;
                    }
                });
    }

    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new TeamCustomerAdapter(getActivity(), mCustomers);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        if (!isPullUp && mCustomers.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
        else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("Id", mCustomers.get(position - 1).getId());
                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
    }


    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //附近的客户
                case R.id.layout_near_customers:
                    Bundle bundle = new Bundle();
                    bundle.putString("position", position);
                    bundle.putSerializable("nearCount", nearCount);
                    bundle.putInt("type", CustomerManagerActivity.NEARCUS_TEAM);//团队2 个人1
                    app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_ZOOM_IN, false, bundle);
                    break;
            }
        }
    };


    /**
     * 刷新列表回调
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerListRushEvent event) {
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
