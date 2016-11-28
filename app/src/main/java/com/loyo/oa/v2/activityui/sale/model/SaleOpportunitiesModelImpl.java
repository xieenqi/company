package com.loyo.oa.v2.activityui.sale.model;

import com.loyo.oa.v2.activityui.other.model.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.SaleOpportunitiesContract;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/11/28
 */

public class SaleOpportunitiesModelImpl implements SaleOpportunitiesContract.Model {

    SaleOpportunitiesContract.Presenter presenter;

    public SaleOpportunitiesModelImpl(SaleOpportunitiesContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void getStageData() {
//        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleStges(new RCallback<ArrayList<SaleStage>>() {
            @Override
            public void success(final ArrayList<SaleStage> saleStages, final Response response) {
                HttpErrorCheck.checkResponse("销售机会 销售阶段:", response);
//                mSaleStages = saleStages;
//                initTitleItem();
//                initChildren();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
//                finish();
            }
        });
    }

}