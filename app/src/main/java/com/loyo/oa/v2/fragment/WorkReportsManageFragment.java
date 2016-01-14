package com.loyo.oa.v2.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.work.WorkReportAddActivity_;
import com.loyo.oa.v2.activity.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activity.work.WorkReportsSearchActivity;
import com.loyo.oa.v2.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.filterview.OnMenuSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 工作报告 界面  的 fragment
 */
public class WorkReportsManageFragment extends BaseCommonMainListFragment<WorkReport> {

    private static final String FILTER_SEND_TYPE[] = new String[]{"全部类别", "提交给我的", "我提交的", "抄送给我的"};
    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待点评", "已点评"};
    private static final String FILTER_TYPE[] = new String[]{"全部类型", "日报", "周报", "月报"};

    private int sendType = 0;
    private int type = 0;
    private int status = 0;
    public int reports;
    private CommonExpandableListAdapter mAdapter;

    @Override
    public void initAdapter() {
        reports=pagingGroupDatas.size();
        mAdapter = new CommonExpandableListAdapter(mActivity, pagingGroupDatas);
        mExpandableListView.getRefreshableView().setAdapter(mAdapter);
    }

    @Override
    public void changeAdapter() {
        mAdapter.setData(pagingGroupDatas);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addNewItem() {
        Intent intent = new Intent();
        intent.setClass(mActivity, WorkReportAddActivity_.class);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    /**跳转搜索*/
    @Override
    public void openSearch() {

        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.WORK_MANAGE);
        intent.putExtras(mBundle);
        intent.setClass(mActivity, WorkReportsSearchActivity.class);
        startActivityForResult(intent, REQUEST_REVIEW);

    }

    @Override
    public void GetData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pagination.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        map.put("reportType", type);
        map.put("sendType", sendType);
        map.put("isReviewed", status);

        //map.put("keyword", "");
        //map.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        //map.put("endAt", System.currentTimeMillis() / 1000);

        LogUtil.dll("客户端发送数据:"+ MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).getWorkReports(map, this);
    }

    @Override
    public void openItem(int groupPosition, int childPosition) {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((WorkReport) mAdapter.getChild(groupPosition, childPosition)).getId());
        intent.setClass(mActivity, WorkReportsInfoActivity_.class);
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    @Override
    public String GetTitle() {
        return "工作报告";
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
        mMenu.setmMenuCount(3);//Menu的个数
        mMenu.setmShowCount(6);//Menu展开list数量最多只显示的个数
        mMenu.setShowCheck(true);//是否显示展开list的选中项
        mMenu.setmMenuTitleTextSize(16);//Menu的文字大小
        mMenu.setmMenuTitleTextColor(getResources().getColor(R.color.title_bg1));//Menu的文字颜色
        mMenu.setmMenuListTextSize(16);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLUE);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.white));//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.ico_make);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setDefaultMenuTitle(new String[]{"全部类别", "全部状态", "全部类型"});//默认未选择任何过滤的Menu title

        List<String[]> items = new ArrayList<>();
        items.add(FILTER_SEND_TYPE);
        items.add(FILTER_STATUS);
        items.add(FILTER_TYPE);

        mMenu.setmMenuItems(items);

        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                app.logUtil.e(" 行 : " + RowIndex + " 列 : " + ColumnIndex);
                switch (ColumnIndex) {
                    case 0:
                        sendType = RowIndex;
                        break;
                    case 1:
                        status = RowIndex;
                        break;
                    case 2:
                        type = RowIndex;
                        break;
                }
                onPullDownToRefresh(mExpandableListView);
            }
        });
    }
}
