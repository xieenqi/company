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

    private Bundle mBundle;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getIntent().getExtras();
        customerType = mBundle.getInt(ExtraAndResult.EXTRA_TYPE);
    }

    @Override
    protected void openDetail(final int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, CustomerDetailInfoActivity_.class);
        intent.putExtra("Customer", adapter.getItem(position));
        startActivity(intent);
    }


    @Override
    public void getData() {
        String url = FinalVariables.SERACH_CLUE_PUBLIC;
        HashMap<String, Object> params = new HashMap<>();

        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        params.put("field", "");
        params.put("status",0);

        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
