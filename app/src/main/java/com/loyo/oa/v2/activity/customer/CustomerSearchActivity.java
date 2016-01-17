package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.v2.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

public class CustomerSearchActivity extends BaseSearchActivity<Customer> {

    private int queryType;
    private Bundle mBundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getIntent().getExtras();
        queryType = mBundle.getInt(ExtraAndResult.CC_DEPARTMENT_NAME);
        LogUtil.dll("来自什么客户:"+queryType);
    }

    @Override
    protected void openDetail(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, CustomerDetailInfoActivity_.class);
        intent.putExtra("Customer", adapter.getItem(position));
        startActivity(intent);
    }


    @Override
    public void getData() {
        Utils.dialogShow(this,"请稍候");
        String url = FinalVariables.SEARCH_CUSTOMERS_SELF;
        HashMap<String, Object> params = new HashMap<>();

        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        params.put("keyWords", strSearch);

        switch (queryType) {
            case 1:
                url = FinalVariables.SEARCH_CUSTOMERS_SELF;
                break;
            case 2:
                url = FinalVariables.SEARCH_CUSTOMERS_TEAM;
                break;
            case 3:
                url = FinalVariables.SEARCH_CUSTOMERS_PUBLIC;
                break;
        }
        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
