package com.loyo.oa.v2.activity.tasks;

import android.content.Intent;

import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.point.ITask;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

/**
 * 【任务 的搜索】 页面
 */
public class TasksSearchActivity extends BaseSearchActivity<Task> {

    @Override
    protected void openDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, TasksInfoActivity_.class);
        intent.putExtra("task", (Task) adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    protected void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", strSearch);
        params.put("endAt", System.currentTimeMillis() / 1000);
        params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd?lstData.size()>=20?lstData.size():20:20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(ITask.class).getTasks(params, this);
    }
}
