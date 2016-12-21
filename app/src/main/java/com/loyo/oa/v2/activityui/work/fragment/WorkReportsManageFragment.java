package com.loyo.oa.v2.activityui.work.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.dropdownmenu.adapter.DefaultMenuAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuModelsSelected;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.adapter.CommonExpandableListAdapter;
import com.loyo.oa.v2.activityui.work.WorkReportAddActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsSearchActivity;
import com.loyo.oa.v2.activityui.work.api.WorkReportService;
import com.loyo.oa.v2.activityui.work.common.WorkReportCategoryMenuModel;
import com.loyo.oa.v2.activityui.work.common.WorkReportStatusMenuModel;
import com.loyo.oa.v2.activityui.work.common.WorkReportTypeMenuModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.network.model.LoyoError;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseCommonMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;

/**
 * 工作报告 界面  的 fragment
 */
public class WorkReportsManageFragment extends BaseCommonMainListFragment<WorkReportRecord> {

    private String typeParam = "0";
    private String statusParam = "0";
    private String categoryParam = "0";
    public int reports;
    private CommonExpandableListAdapter mAdapter;

    @Override
    public void initAdapter() {
        reports = pagingGroupDatas.size();
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
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

    }

    /**
     * 跳转搜索
     */
    @Override
    public void openSearch() {
        Intent intent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.WORK_MANAGE);
        intent.putExtras(mBundle);
        intent.setClass(mActivity, WorkReportsSearchActivity.class);
        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    public void GetData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pagination.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        map.put("reportType", categoryParam);
        map.put("sendType", typeParam);
        map.put("isReviewed", statusParam);

//        LogUtil.dll("客户端发送数据:" + MainApp.gson.toJson(map));
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).getWorkReportsData(map, this);

        //这里借助CallBack接口调用，不能直接实现DefaultLoyoSubscriber，因为会有方法冲突
        WorkReportService.getWorkReportsData(map).subscribe(new DefaultLoyoSubscriber<PaginationX<WorkReportRecord>>(LoyoErrorChecker.LOADING_LAYOUT) {
            @Override
            public void onError(Throwable e) {
                @LoyoErrorChecker.CheckType
                int type= pagination.getPageIndex() != 1 ? LoyoErrorChecker.TOAST : LoyoErrorChecker.COMMIT_DIALOG;
                ((Callback)WorkReportsManageFragment.this).failure(null);
            }

            @Override
            public void onNext(PaginationX<WorkReportRecord> workReportRecordPaginationX) {
                ((Callback)WorkReportsManageFragment.this).success(workReportRecordPaginationX,null);
            }
        });
    }

    @Override
    public void openItem(int groupPosition, int childPosition) {
        WorkReportRecord item = (WorkReportRecord) mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, item.getId());
        if (!item.viewed) {//有红点需要刷新
            intent.putExtra(ExtraAndResult.IS_UPDATE, true);
        }
        intent.setClass(mActivity, WorkReportsInfoActivity_.class);
        startActivityForResult(intent, REQUEST_REVIEW);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    public String GetTitle() {
        return "工作报告";
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
        options.add(WorkReportTypeMenuModel.getFilterModel());
        options.add(WorkReportStatusMenuModel.getFilterModel());
        options.add(WorkReportCategoryMenuModel.getFilterModel());
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
                else if (menuIndex == 2) {
                    categoryParam = key;
                }
                refreshData();
            }
        });
    }
}
