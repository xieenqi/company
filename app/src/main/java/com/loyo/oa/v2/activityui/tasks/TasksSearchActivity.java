package com.loyo.oa.v2.activityui.tasks;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.task.api.TaskService;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

/**
 * 【任务 的搜索】 页面
 */
public class TasksSearchActivity extends BaseSearchActivity<TaskRecord> {

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        //params.put("endAt", System.currentTimeMillis() / 1000);
        //params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());
        params.put("joinType", 0);
        params.put("status", 0);
        subscriber =TaskService.getTasksData(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<TaskRecord>>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        TasksSearchActivity.this.fail(e);
                    }

                    @Override
                    public void onNext(PaginationX<TaskRecord> x) {
                        TasksSearchActivity.this.success(x);
                    }
                });

    }

    @Override
    public void onListItemClick(View view, int position) {
        Intent mIntent = new Intent(getApplicationContext(), TasksInfoActivity_.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position).getId());
        startActivity(mIntent);
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, TaskRecord data) {
        try {
            if (data.planendAt == 0) {
                viewHolder.time.setText("任务截止时间: 无");
            } else {
                viewHolder.time.setText("任务截止时间: " + DateTool.getDateTimeFriendly(data.planendAt));
            }
        } catch (Exception e) {
            Global.ProcException(e);
        }
        if (null != data.responsibleName) {
            viewHolder.content.setText("负责人: " + data.responsibleName);
        }
        if (!TextUtils.isEmpty(data.title)) {
            viewHolder.title.setText(data.title);
        }
    }
}
