package com.loyo.oa.v2.activityui.tasks;

import android.content.Intent;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

/**
 * 【任务 的搜索】 页面
 */
public class TasksSearchActivity extends BaseSearchActivity<TaskRecord> {

    @Override
    protected void openDetail(final int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, TasksInfoActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((Task) adapter.getItem(position)).getId());
        startActivity(intent);
    }

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        //params.put("endAt", System.currentTimeMillis() / 1000);
        //params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd?lstData.size()>=20?lstData.size():20:20);
        params.put("joinType",0);
        params.put("status",0);
        TaskService.getTasksData(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<TaskRecord>>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        expandableListView_search.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<TaskRecord> x) {
                        expandableListView_search.onRefreshComplete();
                        ll_loading.setStatus(LoadingLayout.Success);
                        if (PaginationX.isEmpty(x)) {
                            if (isTopAdd) {
                                ll_loading.setStatus(LoadingLayout.Empty);
                            } else {
                                Toast("没有更多数据!");
                            }
                            return;
                        }
                        paginationX = (PaginationX) x;
                        if (isTopAdd) {
                            lstData.clear();
                        }
                        lstData.addAll(paginationX.getRecords());
                        changeAdapter();
                    }
                });

    }
}
