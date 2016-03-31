package com.loyo.oa.v2.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.project.ProjectAddActivity_;
import com.loyo.oa.v2.activity.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activity.project.ProjectSearchActivity;
import com.loyo.oa.v2.adapter.ProjectExpandableListAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.point.IProject;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.filterview.OnMenuSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :项目列表页
 * 作者 : ykb
 * 时间 : 15/9/7.
 */
public class ProjectManageFragment extends BaseCommonMainListFragment<Project> {

    private static final String[] FILTER_TYPE_ARRAY = new String[]{"全部类型", "我负责", "我创建", "我参与"};
    private static final int[] FILTER_TYPEID_ARRAY = new int[]{0, 3, 2, 1};
    private static final String[] FILTER_STATUS_ARRAY = new String[]{"全部状态", "进行中", "已结束"};
    private ProjectExpandableListAdapter adapter;
    private int type = 0;
    private int status = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void GetData() {
        showLoading("");
        if (lstData == null) {
            lstData = new ArrayList<>();
        }

        HashMap<String,Object> params = new HashMap<>();
        int pageIndex = pagination.getPageIndex();
        params.put("pageIndex", pageIndex);
        int pageSize = isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20;
        params.put("pageSize", pageSize);
        params.put("status", status);
        params.put("type", type);
        params.put("endAt", System.currentTimeMillis()/1000);
        params.put("startAt", DateTool.getDateToTimestamp("2014-01-01",app.df5)/1000);

        LogUtil.d(" 项目管理列表请求： "+ MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IProject.class).getProjects(params,this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        mMenu.setmMenuTitleTextColor(getResources().getColor(R.color.text33));//Menu的文字颜色
        mMenu.setmMenuListTextSize(14);//Menu展开list的文字大小
        mMenu.setmMenuListTextColor(Color.BLACK);//Menu展开list的文字颜色
        mMenu.setmMenuBackColor(Color.WHITE);//Menu的背景颜色
        mMenu.setmMenuPressedBackColor(getResources().getColor(R.color.white));//Menu按下的背景颜色
        mMenu.setmCheckIcon(R.drawable.img_check1);//Menu展开list的勾选图片
        mMenu.setmUpArrow(R.drawable.arrow_up);//Menu默认状态的箭头
        mMenu.setmDownArrow(R.drawable.arrow_down);//Menu按下状态的箭头
        mMenu.setDefaultMenuTitle(new String[]{"全部类型", "全部状态"});//默认未选择任何过滤的Menu title

        List<String[]> items = new ArrayList<>();
        items.add(FILTER_TYPE_ARRAY);
        items.add(FILTER_STATUS_ARRAY);
        mMenu.setmMenuItems(items);

        mMenu.setMenuSelectedListener(new OnMenuSelectedListener() {
            @Override
            //Menu展开的list点击事件  RowIndex：list的索引  ColumnIndex：menu的索引
            public void onSelected(View listview, int RowIndex, int ColumnIndex) {
                app.logUtil.e(" 行 : " + RowIndex + " 列 : " + ColumnIndex);
                if (ColumnIndex == 0) {
                    type = FILTER_TYPEID_ARRAY[RowIndex];
                } else {
                    status = RowIndex;
                }
                onPullDownToRefresh(mExpandableListView);
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
    }

    /**
     * 点击 item的 操作
     * @param groupPosition
     * @param childPosition
     */
    @Override
    public void openItem(int groupPosition, int childPosition) {
        Intent intent = new Intent();
        intent.setClass(mActivity, ProjectInfoActivity_.class);
        intent.putExtra("projectId", ((Project)adapter.getChild(groupPosition, childPosition)).id);
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    @Override
    public String GetTitle() {
        return "项目管理";
    }

    @Override
    public void initTab() {
        initDropMenu();
    }

    /**跳转搜索*/
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
