package com.loyo.oa.v2.activityui.sale.model;


import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.contract.AddIntentionProductContract;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.point.ISale;
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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getProducts(new RCallback<ArrayList<Product>>() {
            @Override
            public void success(final ArrayList<Product> products, final Response response) {
                HttpErrorCheck.checkResponse("意向产品##的产品", response);
                mPersenter.setProduct(products);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                mPersenter.closePage();
            }
        });
    }

    @Override
    public void addProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data) {

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).addSaleProduct(map, new RCallback<SaleProductEdit>() {
            @Override
            public void success(SaleProductEdit saleProductEdit, final Response response) {
                HttpErrorCheck.checkResponse("新增意向产品", response);
                mPersenter.addProductSuccess(data);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void editProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data, String saleId) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).editSaleProduct(map, saleId, new RCallback<SaleProductEdit>() {
            @Override
            public void success(SaleProductEdit saleProductEdit, final Response response) {
                HttpErrorCheck.checkResponse("编辑意向产品", response);
                mPersenter.editProductSuccess(data);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }
}