package com.loyo.oa.v2.activityui.work;

import android.content.Intent;
import android.view.View;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.work.api.WorkReportService;
import com.loyo.oa.v2.activityui.work.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

import retrofit.Callback;


public class WorkReportsSearchActivity extends BaseSearchActivity<WorkReportRecord> {


    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("reportType", 0);
        params.put("keyword", strSearch);
        params.put("sendType", 0);
        params.put("isReviewed", 0);
        //params.put("endAt", System.currentTimeMillis() / 1000);
        //params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());

        WorkReportService.getWorkReportsData(params).subscribe(new DefaultLoyoSubscriber<PaginationX<WorkReportRecord>>(ll_loading) {
            @Override
            public void onError(Throwable e) {
                WorkReportsSearchActivity.this.fail(e);
            }

            @Override
            public void onNext(PaginationX<WorkReportRecord> workReportRecordPaginationX) {
                WorkReportsSearchActivity.this.success(workReportRecordPaginationX);
            }
        });
    }

    @Override
    public void onListItemClick(View view, int position) {
        Intent mIntent = new Intent(getApplicationContext(), WorkReportsInfoActivity_.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position).getId());
        startActivity(mIntent);
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, WorkReportRecord data) {
        if (null != data.reviewerName) {
            viewHolder.content.setText("点评: " + data.reviewerName);
        }
        StringBuilder reportTitle = new StringBuilder(data.title);
        String reportType = "";
        switch (data.type) {
            case WorkReport.DAY:
                reportType = " 日报";
                break;
            case WorkReport.WEEK:
                reportType = " 周报";
                break;
            case WorkReport.MONTH:
                reportType = " 月报";
                break;
        }
        reportTitle.append(reportType);
        if (data.isDelayed) {
            reportTitle.append(" (补签)");
        }
        viewHolder.title.setText(reportTitle);
        String end = "提交时间: " + DateTool.getDateTimeFriendly(data.createdAt);
        viewHolder.time.setText(end);
    }
}
