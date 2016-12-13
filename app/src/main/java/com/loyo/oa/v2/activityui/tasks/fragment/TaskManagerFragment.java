package com.loyo.oa.v2.activityui.tasks.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.activityui.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksSearchActivity;
import com.loyo.oa.v2.activityui.tasks.common.TaskStatusMenuModel;
import com.loyo.oa.v2.activityui.tasks.common.TaskTypeMenuModel;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【任务管理】 界面
 */

public class TaskManagerFragment extends BaseCommonMainListFragment<TaskRecord> {

    private String typeParam = "0";
    private String statusParam = "0";
    private CommonExpandableListAdapter mAdapter;

    @Override
    public void GetData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pagination.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        map.put("joinType", typeParam);
        map.put("status", statusParam);
        map.put("endAt", System.currentTimeMillis() / 1000);
//        map.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        map.put("startAt", com.loyo.oa.common.utils.DateTool.getDateStamp("2014-01-01")/ 1000);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).getTasksData(map, this);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonExpandableListAdapter(mActivity, pagingGroupDatas);
        mExpandableListView.getRefreshableView().setAdapter(mAdapter);
    }

    @Override
    public void changeAdapter() {
        mAdapter.setData(pagingGroupDatas);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 任务管理 底部添加 创建任务 button
     */
    @Override
    public void addNewItem() {
        Intent intent = new Intent();
        intent.setClass(mActivity, TasksAddActivity_.class);
        startActivityForResult(intent, REQUEST_CREATE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    /**
     * 任务管理跳转搜索
     */
    @Override
    public void openSearch() {
        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.TASKS_MANAGE);
        intent.setClass(mActivity, TasksSearchActivity.class);
        intent.putExtras(mBundle);
        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    public void openItem(int groupPosition, int childPosition) {
        TaskRecord item = (TaskRecord) mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, item.getId());
        if (!item.viewed) {//有红点需要刷新
            intent.putExtra(ExtraAndResult.IS_UPDATE, true);
        }
        intent.setClass(mActivity, TasksInfoActivity_.class);

        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    public String GetTitle() {
        return "任务管理";
    }

    @Override
    public void initTab() {
        loadFilterOptions();
    }

    /**
     * 初始化下拉菜单
     */
    private void loadFilterOptions() {
        List<FilterModel> options = new ArrayList<>();
        options.add(TaskTypeMenuModel.getFilterModel());
        options.add(TaskStatusMenuModel.getFilterModel());
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
                onPullDownToRefresh(mExpandableListView);
            }
        });
    }
}
