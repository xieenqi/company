package com.loyo.oa.v2.activityui.sale.fragment;

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
import com.loyo.oa.dropdownmenu.filtermenu.CommonSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.filtermenu.OrganizationFilterModel;
import com.loyo.oa.dropdownmenu.filtermenu.SaleStageMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.adapter.AdapterSaleTeam;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.TeamSaleFragmentContract;
import com.loyo.oa.v2.activityui.sale.model.SaleStageConfig;
import com.loyo.oa.v2.activityui.sale.presenter.TeamSaleFragmentPresenterImpl;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.Permission;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 【团队机会】列表
 * Created by yyy on 16/5/17.
 */
public class TeamSaleFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener2, TeamSaleFragmentContract.View {

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
    private Intent mIntent;
    private AdapterSaleTeam adapterSaleTeam;
    private PullToRefreshListView listView;
    private DropDownMenu filterMenu;
    private ArrayList<SaleStage> mSaleStages;
    private TeamSaleFragmentContract.Presenter mPresenter;
    private LoadingLayout ll_loading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mPresenter = new TeamSaleFragmentPresenterImpl(this);
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
//        mSaleStages = (ArrayList<SaleStage>) getArguments().get("stage");
        mSaleStages = SaleStageConfig.getSaleStage(true);
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                mPresenter.getData();
            }
        });
        listView = (PullToRefreshListView) view.findViewById(R.id.saleteam_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
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
        mPresenter.getData();
    }

    private void loadFilterOptions() {

        List<DBDepartment> depts = new ArrayList<>();
        String title = "部门";
        //为超管或权限为全公司 展示全公司成员
        if (PermissionManager.getInstance().dataRange(BusinessOperation.SALE_OPPORTUNITY)
                == Permission.COMPANY) {
            depts.addAll(OrganizationManager.shareManager().allDepartments());
            title = "全公司";
        }
        //权限为部门 展示我的部门
        else if (PermissionManager.getInstance().dataRange(BusinessOperation.SALE_OPPORTUNITY)
                == Permission.TEAM) {
            depts.addAll(OrganizationManager.shareManager().currentUserDepartments());
            title = "本部门";
        } else {
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
                String xPath = "", userId = "", stageId = "", sortType = "";
                if (menuIndex == 0) { // SaleStage
                    // TODO:
                    if (model.getClass().equals(OrganizationFilterModel.DepartmentMenuModel.class)) {
                        xPath = model.getKey();
                        userId = "";
                    } else if (model.getClass().equals(OrganizationFilterModel.UserMenuModel.class)) {
                        xPath = "";
                        userId = model.getKey();
                    }
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.departmentChanceTeam);
                } else if (menuIndex == 1) { // SaleStage
                    stageId = key;
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.stageChanceTeam);
                } else if (menuIndex == 2) { // 排序
                    sortType = key;
                    UmengAnalytics.umengSend(mActivity, UmengAnalytics.rankChanceTeam);
                }
                mPresenter.getScreenData(stageId, sortType, xPath, userId);
            }
        });
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPresenter.pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPresenter.pullUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            //删除后 刷新列表
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                mPresenter.getData();
                break;

            //新增后 刷新列表
            case ExtraAndResult.REQUEST_CODE_STAGE:
                mPresenter.getData();
                break;
        }

    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void refreshComplete() {
        listView.onRefreshComplete();
    }

    @Override
    public void bindData(ArrayList<SaleRecord> mData) {
        if (null == adapterSaleTeam) {
            adapterSaleTeam = new AdapterSaleTeam(getActivity());
            adapterSaleTeam.setData(mData);
            listView.setAdapter(adapterSaleTeam);
        } else {
            adapterSaleTeam.setData(mData);
        }
    }

    @Override
    public LoadingLayout getLoadingUI() {
        return ll_loading;
    }
}
