package com.loyo.oa.v2.activityui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;

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
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.TeamOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderList;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队订单】
 * Created by xeq on 16/8/1.
 */
public class TeamOrderFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {


    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private TeamOrderAdapter adapter;
    private DropDownMenu filterMenu;

    private String statusType = "0";
    private String field = "";
    private int page = 1;
    private boolean isPullDown = true;

    private List<OrderListItem> listData = new ArrayList<>();
    private String xPath = "", userId = "";
    private Permission permission;


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
        permission = (Permission) getArguments().getSerializable("permission");
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setEmptyView(emptyView);
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

        getData();

    }

    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (permission != null && permission.dataRange == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (permission != null && permission.dataRange == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        }
        else {
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
                    }
                    else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        xPath = "";
                        userId = model.getKey();
                    }
                }
                else if (menuIndex == 1) { //
                    statusType = key;
                }
                else if (menuIndex == 2) { //
                    CommonSortType type = ((CommonSortTypeMenuModel)model).type;
                    if (type == CommonSortType.AMOUNT) {
                        field = "dealMoney";
                    }
                    else if (type == CommonSortType.CREATE) {
                        field = "createdAt";
                    }
                }
                isPullDown = true;
                page = 1;
                getData();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusType);
        map.put("filed", field);
        map.put("xpath", xPath);
        map.put("userId", userId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IOrder.class).getOrderTeamList(map, new Callback<OrderList>() {
            @Override
            public void success(OrderList orderlist, Response response) {
                HttpErrorCheck.checkResponse("团队订单列表：", response);
                lv_list.onRefreshComplete();
                if (!isPullDown) {
                    listData.addAll(orderlist.records);
                } else {
                    listData = orderlist.records;
                }
                adapter.setData(listData);
            }

            @Override
            public void failure(RetrofitError error) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }
}
