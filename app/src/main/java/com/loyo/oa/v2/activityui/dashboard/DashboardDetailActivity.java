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

    private String TAG="DashboardDetailActivity";
    private LinearLayout ll_back;
    private DropDownMenu filterMenu;
    private TextView tv_title;
    private DashborardType type;
    private DashboardDetailAdapter adapter;
    private PullToRefreshListView lv_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        initView();
        getPageData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_dashboard_detail);
    }

    @Override
    public void getPageData() {
        HashMap<String,Object> map= new HashMap<String,Object>();
        map.put("qType",1);
        map.put("sortBy",1);
        map.put("activityObj",1);
        //网络请求
        DashBoardService.getDashBoardListData(map).subscribe(new DefaultLoyoSubscriber<DashBoardListModel>() {
            @Override
            public void onNext(DashBoardListModel dashBoardListModel) {
                Log.i(TAG, "onNext: "+dashBoardListModel);
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
        adapter = new DashboardDetailAdapter(this,type.getTableHead());
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
                Log.d(TAG, "onMenuModelsSelected() called with: menuIndex = [" + menuIndex + "], key:"+key+",value;"+value);

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

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
