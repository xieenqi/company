package com.loyo.oa.v2.activityui.sale.model;

import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.contract.SaleDetailContract;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/11/30
 */

public class SaleDetailModelImpl implements SaleDetailContract.Model {
    SaleDetailContract.Presenter mPersenter;

    public SaleDetailModelImpl(SaleDetailContract.Presenter mPersenter) {
        this.mPersenter = mPersenter;
    }

    @Override
    public void getDataSend(String selectId) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER())
                .create(ISale.class)
                .getSaleDetails(selectId, new RCallback<SaleDetails>() {
                    @Override
                    public void success(SaleDetails saleDetails, Response response) {
                        HttpErrorCheck.checkResponse("机会详情", response, mPersenter.getLoadingView());
                        mPersenter.bindPageData(saleDetails);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error, mPersenter.getLoadingView());
//                        mPersenter.closePge();
                    }
                });
    }

    @Override
    public void editSaleStageSend(HashMap<String, Object> map, String selectId) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER())
                .create(ISale.class)
                .editSaleStage(map, selectId, new RCallback<SaleProductEdit>() {
                    @Override
                    public void success(SaleProductEdit saleProductEdit, Response response) {
                        HttpErrorCheck.checkResponse("编辑销售阶段", response);
                        mPersenter.editSaleStageSuccess();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }
}