package com.loyo.oa.v2.activityui.tasks.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.activityui.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksSearchActivity;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.filterview.OnMenuSelectedListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【任务管理】 界面
 */

public class TaskManagerFragment extends BaseCommonMainListFragment<TaskRecord> {

    private int mJoinType = 0, mStatus = 0;
    private static final String[] TYPE_TAG = new String[]{"全部类型", "我分派的", "我负责的", "我参与的"};
    private static final String[] STATUS_TAG = new String[]{"全部状态", "未完成", "待审核", "已完成"};
    private CommonExpandableListAdapter mAdapter;

    @Override
    public void GetData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pagination.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        map.put("joinType", mJoinType);
        map.put("status", mStatus);
        map.put("endAt", System.currentTimeMillis() / 1000);
        map.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
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
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((TaskRecord) mAdapter.getChild(groupPosition, childPosition)).getId());
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
        initDropMenu();
    }

    /**
     * 初始化下拉菜单
     */
    private void initDropMenu() {

        mMenu.setVisibility(View.VISIBLE);
        mMenu.setmMenuCount(2);//Menu的个数
        mMenu.setmShowCount(6);//Menu展开list数量最多只显示的个数
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(14);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(getResources().getColor(R.color.default_menu_press_text));//Menu的文字颜色
        mMenu.setmMenuListTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.white));//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.img_check1);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setDefaultMenuTitle(new String[]{"全部类型", "全部状态", "全部类型"});//默认未选择任何过滤的Menu title

        List<String[]> items = new ArrayList<>();
        items.add(TYPE_TAG);
        items.add(STATUS_TAG);

        mMenu.setmMenuItems(items);

        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                app.logUtil.e(" 行 : " + RowIndex + " 列 : " + ColumnIndex);
                switch (ColumnIndex) {
                    case 0:
                        mJoinType = RowIndex;
                        break;
                    case 1:
                        mStatus = RowIndex;
                        break;
                }
                onPullDownToRefresh(mExpandableListView);
            }
        });
    }
}
