package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

/**
 * 【线索搜索】
 *
 * Create by yyy on 16/08/23
 * */

public class ClueSearchActivity extends BaseSearchActivity<Customer> {

    private Intent mIntent;
    private Bundle mBundle;
    private int    fromPage;
    private String url;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getIntent().getExtras();
        fromPage = mBundle.getInt(ExtraAndResult.EXTRA_TYPE);
    }

    @Override
    protected void openDetail(final int position) {
        mIntent = new Intent();
        mIntent.setClass(mContext, CustomerDetailInfoActivity_.class);
        mIntent.putExtra("Customer", adapter.getItem(position));
        startActivity(mIntent);
    }


    @Override
    public void getData() {
        if(fromPage == 1){
            url = FinalVariables.SERACH_CLUE_MY;
        }else if(fromPage == 3){
            url = FinalVariables.SERACH_CLUE_TEAM;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        params.put("field", "");
        params.put("status",0);
        params.put("keyword",strSearch);

        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
