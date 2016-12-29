package com.loyo.oa.v2.activityui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.DashboardFilterTimeModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.adapter.DashboardDetailAdapter;
import com.loyo.oa.v2.activityui.dashboard.api.DashBoardService;
import com.loyo.oa.v2.activityui.dashboard.common.DashborardType;
import com.loyo.oa.v2.activityui.dashboard.model.DashBoardListModel;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseLoadingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 【仪表盘】详情页面
 * Created by xeq on 16/12/12.
 */

public class DashboardDetailActivity extends BaseLoadingActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private String TAG = "DashboardDetailActivity";
    private LinearLayout ll_back;
    private DropDownMenu filterMenu;
    private TextView tv_title;
    private DashborardType type;
    private DashboardDetailAdapter adapter;
    private PullToRefreshListView lv_list;

    private int pageIndex = 1;
    private HashMap<String, Object> map = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        initView();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", "30");
        map.put("qType", "1");
        map.put("sortBy", "1");
        getPageData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_dashboard_detail);
    }

    @Override
    public void getPageData() {

        //根据type，判断请求的类型，构造参数
        if (DashborardType.CUS_FOLLOWUP == type) {
            //客户跟进
            map.put("activityObj", 1);
        } else if (DashborardType.SALE_FOLLOWUP == type) {
            //线索跟进
            map.put("activityObj", 2);
        } else if (DashborardType.CUS_CELL_RECORD == type) {
            //客户电话录
            map.put("activityObj", 1);
        } else if (DashborardType.CUS_SIGNIN == type) {
            //客户拜访
        } else if (DashborardType.SALE_CELL_RECORD == type) {
            //获取线索电话录
            map.put("activityObj", 2);
        } else if (DashborardType.COMMON == type) {
            //增量/存量
            map.put("tagItemId", getIntent().getStringExtra("tagItemId"));//tagItemId
            Log.i(TAG, "getPageData: "+getIntent().getStringExtra("tagItemId"));
        } else if (DashborardType.ORDER_NUMBER == type || DashborardType.ORDER_MONEY == type) {
            //订单数量，订单金额
        }

        //网络请求
        DashBoardService.getDashBoardListData(map, type).subscribe(new DefaultLoyoSubscriber<DashBoardListModel>() {
            @Override
            public void onNext(DashBoardListModel dashBoardListModel) {
                if (1 == pageIndex) {
                    adapter.reload(dashBoardListModel.data.records);
                } else {
                    adapter.addAll(dashBoardListModel.data.records);
                }
                lv_list.onRefreshComplete();

            }
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        //获取传入的类型数据，表示是哪种列表
        type = (DashborardType) intent.getSerializableExtra("type");

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(this);
        filterMenu = (DropDownMenu) findViewById(R.id.drop_down_menu);
        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapter = new DashboardDetailAdapter(this, type);
        lv_list.setAdapter(adapter);

        ll_loading.setStatus(LoadingLayout.Success);
        loadFilterOptions();
        tv_title.setText(type.getTitle());
    }

    private void loadFilterOptions() {
        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //TODO 数据权限具体调整
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(type.getaPermission())
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(type.getaPermission())
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        } else {
            title = "我";
            depts.add(OrganizationFilterModel.selfDepartment());
        }
        //添加3个筛选字段
        List<FilterModel> options = new ArrayList<>();
        options.add("订单金额".equals(type.getTitle()) ? DashboardFilterTimeModel.getDashboardOrderMOneyFilterModel() : DashboardFilterTimeModel.getFilterModel());
        options.add(type.getSort());
        options.add(new OrganizationFilterModel(depts, title));

        DefaultMenuAdapter adapter = new DefaultMenuAdapter(this, options);
        filterMenu.setMenuAdapter(adapter);
        //筛选菜单的回调
        adapter.setCallback(new OnMenuModelsSelected() {
            @Override
            public void onMenuModelsSelected(int menuIndex, List<MenuModel> selectedModels, Object userInfo) {
                filterMenu.close();
                MenuModel model = selectedModels.get(0);
                String key = model.getKey();
                String value = model.getValue();
                filterMenu.headerTabBar.setTitleAtPosition(value, menuIndex);
                Log.d(TAG, "onMenuModelsSelected() called with: menuIndex = [" + menuIndex + "], key:" + key + ",value;" + value);
                if (0 == menuIndex) {
                    //时间
                    map.put("qType",key);
                } else if (1 == menuIndex) {
                    //排序
                    map.put("sortBy",key);
                } else if (2 == menuIndex) {
                    //部门或者人筛选
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        map.put("xPath",model.getKey());
                        map.remove("userId");
                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        map.put("userId",model.getKey());
                        map.remove("xPath");
                    }
                }
                getPageData();

//
//                if (menuIndex == 0) { //
////                    statusType = key;
//                } else if (menuIndex == 1) { //
//                    CommonSortType type = ((CommonSortTypeMenuModel) model).type;
//                    if (type == CommonSortType.AMOUNT) {
////                        field = "dealMoney";
//                    } else if (type == CommonSortType.CREATE) {
////                        field = "createdAt";
//                    }
//                } else if (menuIndex == 2) { //
//                    // TODO:
//                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
////                        xPath = model.getKey();
////                        userId = "";
//                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
////                        xPath = "";
////                        userId = model.getKey();
//                    }
//                }
////                ll_loading.setStatus(LoadingLayout.Loading);
////                isPullDown = true;
////                page = 1;
////                getData();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
        }

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        Log.i(TAG, "onPullDownToRefresh: ");
        pageIndex = 1;
        getPageData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        Log.i(TAG, "onPullUpToRefresh: ");
        pageIndex++;
        getPageData();
    }
}
