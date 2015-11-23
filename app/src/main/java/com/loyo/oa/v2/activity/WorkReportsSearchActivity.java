package com.loyo.oa.v2.activity;

import android.content.Intent;

import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;


public class WorkReportsSearchActivity extends BaseSearchActivity<WorkReport> {

    @Override
    protected void openDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, WorkReportsInfoActivity_.class);
        intent.putExtra("workreport", (WorkReport) adapter.getItem(position));
        startActivityForResult(intent, WorkReportsManageFragment.REQUEST_REVIEW);
    }


    @Override
    protected void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("sendType", 0);
        params.put("keyword", strSearch);
        params.put("reportType", 0);
        params.put("status", 0);
        params.put("endAt", System.currentTimeMillis() / 1000);
        params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd?lstData.size()>=20?lstData.size():20:20);

        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).getWorkReports(params, this);
    }
}
