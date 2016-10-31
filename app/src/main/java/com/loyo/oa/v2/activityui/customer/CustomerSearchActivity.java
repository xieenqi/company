package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

public class CustomerSearchActivity extends BaseSearchActivity<Customer> {

    private int customerType;
    private Bundle mBundle;
    public static final int CUSTOMERS_SELF = 1, CUSTOMERS_TEAM = 2, CUSTOMERS_PUBLIC = 3;


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
        String url = FinalVariables.SEARCH_CUSTOMERS_SELF;
        HashMap<String, Object> params = new HashMap<>();

        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        params.put("keyWords", strSearch);

        switch (customerType) {
            case CUSTOMERS_SELF:
                url = FinalVariables.SEARCH_CUSTOMERS_SELF;
                break;
            case CUSTOMERS_TEAM:
                url = FinalVariables.SEARCH_CUSTOMERS_TEAM;
                break;
            case CUSTOMERS_PUBLIC:
                url = FinalVariables.SEARCH_CUSTOMERS_PUBLIC;
                break;
        }
        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
