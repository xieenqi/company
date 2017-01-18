package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;
import android.view.View;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.activityui.work.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

import retrofit.Callback;


public class WfinstanceSearchActivity extends BaseSearchActivity<WfInstanceRecord> {


    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyWords", strSearch);
        map.put("type", 0);
        map.put("status", 0);

        WfinstanceService.getWfInstancesData(map).subscribe(new DefaultLoyoSubscriber<PaginationX<WfInstanceRecord>>(ll_loading) {
            @Override
            public void onNext(PaginationX<WfInstanceRecord> wfInstanceRecordPaginationX) {
                WfinstanceSearchActivity.this.success(wfInstanceRecordPaginationX);
            }

            @Override
            public void onError(Throwable e) {
                WfinstanceSearchActivity.this.fail(e);

            }
        });
    }

    @Override
    public void onListItemClick(View view, int position) {
        Intent mIntent = new Intent(getApplicationContext(), WfinstanceInfoActivity_.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position ).getId());
        startActivity(mIntent);
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, WfInstanceRecord data) {
        if (data.title != null) {
            viewHolder.title.setText(data.title);
        }
        viewHolder.time.setText("提交时间: " + DateTool.getDateTimeFriendly(data.createdAt));
        if (null != data.nextExecutorName) {
            viewHolder.content.setText(String.format("申请人 %s", data.nextExecutorName));
        }
    }
}
