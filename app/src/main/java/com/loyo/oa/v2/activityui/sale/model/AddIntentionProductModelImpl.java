package com.loyo.oa.v2.activityui.sale.model;


import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.contract.AddIntentionProductContract;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by xeq on 2016/11/30
 */

public class AddIntentionProductModelImpl implements AddIntentionProductContract.Model {

    private AddIntentionProductContract.Presenter mPersenter;

    public AddIntentionProductModelImpl(AddIntentionProductContract.Presenter mPersenter) {
        this.mPersenter = mPersenter;
    }

    @Override
    public void getProductData() {
        CustomerService.getProducts()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Product>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        DialogHelp.cancelLoading();
                        mPersenter.closePage();
                    }

                    @Override
                    public void onNext(ArrayList<Product> products) {
                        DialogHelp.cancelLoading();
                        mPersenter.setProduct(products);
                    }
                });
    }

    @Override
    public void addProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data) {

//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).addSaleProduct(map, new RCallback<SaleProductEdit>() {
//            @Override
//            public void success(SaleProductEdit saleProductEdit, final Response response) {
//                HttpErrorCheck.checkResponse("新增意向产品", response);
//                mPersenter.addProductSuccess(data);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//            }
//        });

        SaleService.addSaleProduct(map).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                DialogHelp.cancelLoading();
                mPersenter.addProductSuccess(data);
            }
        });
    }

    @Override
    public void editProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data, String saleId) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).editSaleProduct(map, saleId, new RCallback<SaleProductEdit>() {
//            @Override
//            public void success(SaleProductEdit saleProductEdit, final Response response) {
//                HttpErrorCheck.checkResponse("编辑意向产品", response);
//                mPersenter.editProductSuccess(data);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//            }
//        });

        SaleService.editSaleProduct(map,saleId).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>() {
            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                DialogHelp.cancelLoading();
                mPersenter.editProductSuccess(data);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }
        });
    }
}