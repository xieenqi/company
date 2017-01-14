package com.loyo.oa.v2.activityui.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.project.ProjectAddActivity_;
import com.loyo.oa.v2.activityui.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activityui.project.ProjectSearchActivity;
import com.loyo.oa.v2.activityui.project.adapter.ProjectExpandableListAdapter;
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.activityui.project.common.ProjectStatusMenuModel;
import com.loyo.oa.v2.activityui.project.common.ProjectTypeMenuModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :项目列表页
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public class ProjectManageFragment extends BaseCommonMainListFragment<Project> {

    private ProjectExpandableListAdapter adapter;
    private String typeParam = "0";
    private String statusParam = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void GetData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pagination.getShouldLoadPageIndex());
        params.put("pageSize", pagination.getPageSize());
        params.put("status", statusParam);
        params.put("type", typeParam);
        params.put("endAt", System.currentTimeMillis() / 1000);
        params.put("startAt", com.loyo.oa.common.utils.DateTool.getDateStamp("2014-01-01") / 1000);
        ProjectService.getProjects(params).subscribe(new DefaultLoyoSubscriber<PaginationX<Project>>(ll_loading) {
            @Override
            public void onNext(PaginationX<Project> projectPaginationX) {
                ProjectManageFragment.this.success(projectPaginationX);
            }

            @Override
            public void onError(Throwable e) {
                ProjectManageFragment.this.fail(e);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化下拉菜单
     */
    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(ProjectTypeMenuModel.getFilterModel());
        options.add(ProjectStatusMenuModel.getFilterModel());
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

                if (menuIndex == 0) {
                    typeParam = key;
                }
                else if (menuIndex == 1) {
                    statusParam = key;
                }
                refreshData();
            }
        });
    }

    @Override
    public void initAdapter() {
        if (null == adapter) {
            adapter = new ProjectExpandableListAdapter(mActivity, pagingGroupDatas);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }

    @Override
    public void changeAdapter() {
        if (null == adapter) {
            initAdapter();
            return;
        }
        adapter.setData(pagingGroupDatas);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addNewItem() {
        Intent intent = new Intent();
        intent.setClass(mActivity, ProjectAddActivity_.class);
        startActivityForResult(intent, REQUEST_CREATE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 点击 item的 操作
     *
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void openItem(int groupPosition, int childPosition) {
        Project item = (Project) adapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent();
        intent.setClass(mActivity, ProjectInfoActivity_.class);
        intent.putExtra("projectId", item.id);
        if (!item.viewed) {//有红点需要刷新
            intent.putExtra(ExtraAndResult.IS_UPDATE, true);
        }
        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    public String GetTitle() {
        return "项目管理";
    }

    @Override
    public void initTab() {
        loadFilterOptions();
    }

    /**
     * 跳转搜索
     */
    @Override
    public void openSearch() {
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.PEOJECT_MANAGE);
        app.startActivity(getActivity(), ProjectSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
