package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.io.IOException;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerSearchActivity extends BaseSearchActivity<Customer> {

    private int queryType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryType=getIntent().getIntExtra("queryType",-1);
    }

    @Override
    protected void openDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, CustomerDetailInfoActivity_.class);
        intent.putExtra("Customer", adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    protected void getData() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd?lstData.size()>=20?lstData.size():20:20);
        params.put("keyword", strSearch);

        String url=FinalVariables.SEARCH_CUSTOMERS_SELF;
        switch (queryType){
            case 1:
                url=FinalVariables.SEARCH_CUSTOMERS_SELF;
                break;
            case 2:
                url=FinalVariables.SEARCH_CUSTOMERS_TEAM;
                break;
            case 3:
                url = FinalVariables.SEARCH_CUSTOMERS_PUBLIC;
                break;
        }
        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
