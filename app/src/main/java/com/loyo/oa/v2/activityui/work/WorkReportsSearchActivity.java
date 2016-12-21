package com.loyo.oa.v2.activityui.work;

import android.content.Intent;

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

    private final int pageSize = 20;

    @Override
    protected void openDetail(final int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, WorkReportsInfoActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, ((WorkReport) adapter.getItem(position)).getId());
        startActivityForResult(intent, WorkReportsManageFragment.REQUEST_REVIEW);
    }

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("reportType", 0);
        params.put("keyword", strSearch);
        params.put("sendType", 0);
        params.put("isReviewed", 0);
        //params.put("endAt", System.currentTimeMillis() / 1000);
        //params.put("startAt", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd?lstData.size()>=pageSize?lstData.size():pageSize:pageSize);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).getWorkReportsData(params, this);

        WorkReportService.getWorkReportsData(params).subscribe(new DefaultLoyoSubscriber<PaginationX<WorkReportRecord>>(ll_loading) {
            @Override
            public void onError(Throwable e) {
                ((Callback)WorkReportsSearchActivity.this).failure(null);
            }

            @Override
            public void onNext(PaginationX<WorkReportRecord> workReportRecordPaginationX) {
                ((Callback)WorkReportsSearchActivity.this).success(workReportRecordPaginationX,null);
            }
        });
    }
}
