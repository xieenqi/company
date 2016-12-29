package com.loyo.oa.v2.activityui.sale.model;


import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleOpportunity;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.AddMySaleContract;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
//                create(ISale.class).getSaleFild(new Callback<ArrayList<ContactLeftExtras>>() {
//            @Override
//            public void success(ArrayList<ContactLeftExtras> bulletinPaginationX, Response response) {
//                HttpErrorCheck.checkResponse("销售机会动态字段：", response);
//                mPersenter.setDynamic(bulletinPaginationX);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        SaleService.getSaleField().subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>() {
            @Override
            public void onNext(ArrayList<ContactLeftExtras> bulletinPaginationX) {
                mPersenter.setDynamic(bulletinPaginationX);
            }
        });
    }

    @Override
    public void getSaleStageData() {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
//                create(ISale.class).getSaleStage(new Callback<ArrayList<SaleStage>>() {
//            @Override
//            public void success(ArrayList<SaleStage> saleStage, Response response) {
//                HttpErrorCheck.checkResponse("销售阶段", response);
//                mPersenter.setStage(saleStage);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//            }
//        });

        SaleService.getSaleStage().subscribe(new DefaultLoyoSubscriber<ArrayList<SaleStage>>() {
            @Override
            public void onNext(ArrayList<SaleStage> saleStages) {
                mPersenter.setStage(saleStages);
            }
        });
    }

    @Override
    public void creatSaleSend(HashMap<String, Object> map) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
//                create(ISale.class).addSaleOpportunity(map, new Callback<SaleOpportunity>() {
//            @Override
//            public void success(SaleOpportunity saleOpportunity, Response response) {
//                HttpErrorCheck.checkCommitSus("创建销售机会",response);
//                mPersenter.creatSaleSuccess();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkCommitEro(error);
//            }
//        });

        SaleService.addSaleOpportunity(map)
                .subscribe(new DefaultLoyoSubscriber<SaleOpportunity>(mPersenter.getHUD()) {
            @Override
            public void onNext(SaleOpportunity saleOpportunity) {
                mPersenter.creatSaleSuccess();
            }
        });
    }

    @Override
    public void editSaleSend(HashMap<String, Object> map, String chanceId) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
//                create(ISale.class).updateSaleOpportunity(map, chanceId, new Callback<SaleOpportunity>() {
//            @Override
//            public void success(SaleOpportunity saleOpportunity, Response response) {
//                HttpErrorCheck.checkCommitSus("编辑销售机会",response);
//                mPersenter.editSaleSuccess();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkCommitEro(error);
//            }
//        });


        SaleService.updateSaleOpportunity(map, chanceId)
                .subscribe(new DefaultLoyoSubscriber<SaleOpportunity>(mPersenter.getHUD()) {
            @Override
            public void onNext(SaleOpportunity saleOpportunity) {
                mPersenter.editSaleSuccess();
            }
        });


    }
}