package com.loyo.oa.v2.activityui.sale.model;

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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(I2Customer.class).getSaleStges(new RCallback<ArrayList<SaleStage>>() {
//            @Override
//            public void success(final ArrayList<SaleStage> saleStages, final Response response) {
//                HttpErrorCheck.checkResponse("销售机会 model销售阶段:", response);
//                presenter.sendPageData(saleStages);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//                presenter.closePage();
//            }
//        });
    }

}