package com.loyo.oa.v2.activityui.customer.presenter.impl;

import com.loyo.oa.v2.activityui.customer.presenter.DynamicSelectCustomerAndCuleFragmentPersener;
import com.loyo.oa.v2.activityui.customer.viewcontrol.DynamicSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 跟进对象 选择客户 和 选择线索 公用的一个 persenter
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectCustomerAndCuleFragmentPCersener implements DynamicSelectCustomerAndCuleFragmentPersener {
    public static final int SELECT_CUSTOMER = 101;
    public static final int SELECT_CULE = 102;
    private DynamicSelectCustomerAndCuleFragmentVControl vControl;
    private int type;

    public DynamicSelectCustomerAndCuleFragmentPCersener(DynamicSelectCustomerAndCuleFragmentVControl vControl, int type) {
        this.type = type;
        this.vControl = vControl;
    }

    @Override
    public void getPageData(Object... pag) {
        if (type == SELECT_CUSTOMER) {
            getCustomerData();
        } else if (type == SELECT_CULE) {
            getCuleData();
        }
    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    private void getCustomerData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 15);
        LogUtil.d("我的客户查询参数：" + MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_MY).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> customerPaginationX, Response response) {
                        HttpErrorCheck.checkResponse("我的客户", response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                }
        );
    }

    private void getCuleData() {

    }

}
