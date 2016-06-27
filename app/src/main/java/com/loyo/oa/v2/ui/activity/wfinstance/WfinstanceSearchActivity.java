package com.loyo.oa.v2.ui.activity.wfinstance;

import android.content.Intent;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.ui.activity.work.fragment.WorkReportsManageFragment;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.HashMap;


public class WfinstanceSearchActivity extends BaseSearchActivity<WfInstance> {

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

        RestAdapterFactory.getInstance().build(Config_project.API_URL()+ FinalVariables.wfinstance).create(IWfInstance.class).getWfInstances(map, this);
    }
}
