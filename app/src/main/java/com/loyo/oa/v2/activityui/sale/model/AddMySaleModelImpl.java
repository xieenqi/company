package com.loyo.oa.v2.activityui.sale.model;

import android.content.Intent;

import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.SaleOpportunityAdd;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.AddMySaleContract;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/11/30
 */

public class AddMySaleModelImpl implements AddMySaleContract.Model {

    AddMySaleContract.Presenter mPersenter;

    public AddMySaleModelImpl(AddMySaleContract.Presenter mPersenter) {
        this.mPersenter = mPersenter;
    }

    @Override
    public void getDynamicInfo() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).getSaleFild(new Callback<ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(ArrayList<ContactLeftExtras> bulletinPaginationX, Response response) {
                HttpErrorCheck.checkResponse("销售机会动态字段：", response);
                mPersenter.setDynamic(bulletinPaginationX);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });

    }

    @Override
    public void getSaleStageData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).getSaleStage(new Callback<ArrayList<SaleStage>>() {
            @Override
            public void success(ArrayList<SaleStage> saleStage, Response response) {
                HttpErrorCheck.checkResponse("销售阶段", response);
                mPersenter.setStage(saleStage);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void creatSaleSend(HashMap<String, Object> map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).addSaleOpportunity(map, new Callback<SaleOpportunityAdd>() {
            @Override
            public void success(SaleOpportunityAdd saleOpportunityAdd, Response response) {
                HttpErrorCheck.checkResponse("创建销售机会", response);
                mPersenter.creatSaleSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void editSaleSend(HashMap<String, Object> map, String chanceId) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).updateSaleOpportunity(map, chanceId, new Callback<SaleOpportunityAdd>() {
            @Override
            public void success(SaleOpportunityAdd saleOpportunityAdd, Response response) {
                HttpErrorCheck.checkResponse("修改销售机会", response);
                mPersenter.editSaleSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }
}