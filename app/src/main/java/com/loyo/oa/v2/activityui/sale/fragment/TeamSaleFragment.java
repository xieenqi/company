package com.loyo.oa.v2.activityui.sale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.loyo.oa.dropdownmenu.DropDownMenu;
import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.SaleStageMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.SaleStage;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterSaleTeam;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【团队机会】列表
 * Created by yyy on 16/5/17.
 */
public class TeamSaleFragment extends BaseFragment implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2 {

    /**
     * 部门筛选回调
     */
    public final static int SALETEAM_SCREEN_TAG1 = 0X01;

    /**
     * 销售阶段回调
     */
    public final static int SALETEAM_SCREEN_TAG2 = 0X02;

    /**
     * 排序回调
     */
    public final static int SALETEAM_SCREEN_TAG3 = 0X03;

    private View mView;
    private Button btn_add;
    private Intent mIntent;
    private ViewStub emptyView;
    private RelativeLayout rl_nodata;
    private AdapterSaleTeam adapterSaleTeam;
    private PullToRefreshListView listView;
    private DropDownMenu filterMenu;

    private ArrayList<SaleStage> mSaleStages;
    private ArrayList<SaleRecord> mData = new ArrayList<>();
    private boolean isPull = false;
    private int requestPage = 1;
    private String xPath = "";
    private String sortType = "";
    private String userId = "";
    private String stageId = "";
    private Permission permission;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_sale, null);
            initView(mView);
            loadFilterOptions();
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void initView(View view) {
        permission = (Permission) getArguments().getSerializable("permission");
        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");

        listView = (PullToRefreshListView) view.findViewById(R.id.saleteam_list);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        rl_nodata = (RelativeLayout) view.findViewById(R.id.rl_nodata);
        btn_add = (Button) view.findViewById(R.id.btn_add);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, true);
                mIntent.putExtra("id", adapterSaleTeam.getData().get(position - 1).getId());
                mIntent.setClass(getActivity(), SaleDetailsActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        filterMenu = (DropDownMenu) view.findViewById(R.id.drop_down_menu);

        showLoading("");
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
        options.add(SaleStageMenuModel.getStageFilterModel(mSaleStages));
        options.add(CommonSortTypeMenuModel.getFilterModel());
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

                if (menuIndex == 0) { // SaleStage
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
                else if (menuIndex == 1) { // SaleStage
                    stageId = key;
                }
                else if (menuIndex == 2) { // 排序
                    sortType = key;
                }
                requestPage = 1;
                isPull = false;
                getRefershData();
            }
        });
    }

    private void getRefershData() {
        requestPage = 1;
        isPull = false;
        getData();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        requestPage = 1;
        isPull = false;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPull = true;
        requestPage++;
        getData();
    }

    /**
     * 获取 团队机会列表
     */
    public void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", requestPage);
        map.put("pageSize", 15);
        map.put("stageId", stageId);
        map.put("sortType", sortType);
        map.put("xpath", xPath);
        map.put("userId", userId);
        LogUtil.d("团队机会列表 请求数据:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleTeamList(map, new RCallback<SaleList>() {
            @Override
            public void success(SaleList saleTeamList, Response response) {
                HttpErrorCheck.checkResponse("客户列表", response);
                if (null == saleTeamList.records || saleTeamList.records.size() == 0) {
                    if (isPull) {
                        Toast("没有更多数据了!");
                    } else {
                        mData.clear();
                        rl_nodata.setVisibility(View.VISIBLE);
                    }
                    listView.onRefreshComplete();
                } else {
                    if (isPull) {
                        mData.addAll(saleTeamList.records);
                    } else {
                        mData.clear();
                        mData = saleTeamList.records;
                    }
                }
                bindData();
                listView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
            }
        });
    }

    public void bindData() {
        if (null == adapterSaleTeam) {
            adapterSaleTeam = new AdapterSaleTeam(getActivity());
            adapterSaleTeam.setData(mData);
            listView.setAdapter(adapterSaleTeam);
        } else {
            adapterSaleTeam.setData(mData);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //删除后 刷新列表
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                getData();
                break;

            //新增后 刷新列表
            case ExtraAndResult.REQUEST_CODE_STAGE:
                getData();
                break;

            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建机会
            case R.id.btn_add:

                mIntent = new Intent();
                mIntent.setClass(getActivity(), AddMySaleActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);

                break;

            default:
                break;
        }
    }
}
