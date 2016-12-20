package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;

import com.loyo.oa.v2.activityui.wfinstance.api.WfinstanceService;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.activityui.work.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.HashMap;

import okhttp3.Call;
import retrofit.Callback;


public class WfinstanceSearchActivity extends BaseSearchActivity<WfInstanceRecord> {

    @Override
    protected void openDetail(final int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, WfinstanceInfoActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, (adapter.getItem(position)).getId());
        startActivityForResult(intent, WorkReportsManageFragment.REQUEST_REVIEW);
    }

    @Override
    public void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", paginationX.getPageIndex());
        map.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
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
                ((Callback)WfinstanceSearchActivity.this).success(wfInstanceRecordPaginationX,null);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ((Callback)WfinstanceSearchActivity.this).failure(null);
            }
        });
    }
}
