package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;

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

//    @Override
//    protected void openDetail(final int position) {
//        Intent intent = new Intent();
//        intent.setClass(mContext, WfinstanceInfoActivity_.class);
//        intent.putExtra(ExtraAndResult.EXTRA_ID, (adapter.getItem(position)).getId());
//        startActivityForResult(intent, WorkReportsManageFragment.REQUEST_REVIEW);
//    }

    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getShouldLoadPageIndex());
        map.put("pageSize", paginationX.getPageSize());
        map.put("keyWords", strSearch);
        map.put("type", 0);
        map.put("status", 0);
        //map.put("bizformId", "");
        //map.put("endTime", System.currentTimeMillis() / 1000);
       // map.put("beginTime", DateTool.getDateToTimestamp("2014-01-01", app.df5) / 1000);

//        RestAdapterFactory.getInstance().build(Config_project.API_URL()+ FinalVariables.wfinstance).
//                create(IWfInstance.class).getWfInstancesData(map, this);

        // 这里掉用很麻烦，所以，把CallBack当接口使用，方便完成一些其他界面的操作
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
}
