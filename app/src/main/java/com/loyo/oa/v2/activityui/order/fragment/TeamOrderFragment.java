package com.loyo.oa.v2.activityui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortType;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrderStatusMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.TeamOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【团队订单】
 * Created by xeq on 16/8/1.
 */
public class TeamOrderFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {


    private LoadingLayout ll_loading;
    private PullToRefreshListView lv_list;
    private TeamOrderAdapter adapter;
    private DropDownMenu filterMenu;

    private String statusType = "0";
    private String field = "";
    private String xPath = "", userId = "";
    private PaginationX<OrderListItem> paginationX=new PaginationX<>(20);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_order, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    private void initView(View view) {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
//                mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, adapter.getItemData(position - 1).id);
                mIntent.setClass(getActivity(), OrderDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        adapter = new TeamOrderAdapter(app);
        lv_list.setAdapter(adapter);
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                getPageData();
            }
        });
        getPageData();
    }

    private void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        paginationX.setFirstPage();
        getData();
    }

    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(BusinessOperation.ORDER_MANAGEMENT)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.ORDER_MANAGEMENT)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        } else {
            title = "我";
            depts.add(OrganizationFilterModel.selfDepartment());
        }

        List<FilterModel> options = new ArrayList<>();
        options.add(new OrganizationFilterModel(depts, title));
        options.add(OrderStatusMenuModel.getFilterModel());
        List<MenuModel> sortModel = new ArrayList<>();
        sortModel.add(new CommonSortTypeMenuModel(CommonSortType.CREATE));
        sortModel.add(new CommonSortTypeMenuModel(CommonSortType.AMOUNT));
        options.add(new FilterModel(sortModel, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL));

        DefaultMenuAdapter adapter = new DefaultMenuAdapter(getContext(), options);
        filterMenu.setMenuAdapter(adapter);
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);

                if (menuIndex == 0) { //
                    // TODO:
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        xPath = model.getKey();
                        userId = "";
                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        xPath = "";
                        userId = model.getKey();
                    }
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.departmentOrderTeam);
                } else if (menuIndex == 1) { //
                    statusType = key;
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.stageOrderTeam);
                } else if (menuIndex == 2) { //
                    CommonSortType type = ((CommonSortTypeMenuModel) model).type;
                    if (type == CommonSortType.AMOUNT) {
                        field = "dealMoney";
                    } else if (type == CommonSortType.CREATE) {
                        field = "createdAt";
                    }
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.rankOrderTeam);
                }
                getPageData();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("status", statusType);
        map.put("filed", field);
        map.put("xpath", xPath);
        map.put("userId", userId);
        OrderService.getOrderTeamList(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<OrderListItem>>() {

                    @Override
                    public void onError(Throwable e) {
                        @LoyoErrorChecker.CheckType
                        int type = paginationX.isEnpty() ?LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST ;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        lv_list.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<OrderListItem> orderListItemPaginationX) {
                        lv_list.onRefreshComplete();
                        ll_loading.setStatus(LoadingLayout.Success);
                        paginationX.loadRecords(orderListItemPaginationX);
                        if(paginationX.isEnpty()){
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }else{
                            adapter.setData(paginationX.getRecords());
                            if(paginationX.isNeedToBackTop()){
                                lv_list.getRefreshableView().setSelection(0);
                            }
                        }
                    }
                });

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        paginationX.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getData();
    }
}
