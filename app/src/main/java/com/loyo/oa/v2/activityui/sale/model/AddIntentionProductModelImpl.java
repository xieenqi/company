package com.loyo.oa.v2.activityui.sale.model;


import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.contract.AddIntentionProductContract;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.ArrayList;
import java.util.HashMap;

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
                        mPersenter.closePage();
                    }

                    @Override
                    public void onNext(ArrayList<Product> products) {
                        mPersenter.setProduct(products);
                    }
                });
    }

    @Override
    public void addProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data) {
        SaleService.addSaleProduct(map).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>() {
            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                mPersenter.addProductSuccess(data);
            }
        });
    }

    @Override
    public void editProductSend(HashMap<String, Object> map, final SaleIntentionalProduct data, String saleId) {
        SaleService.editSaleProduct(map,saleId).subscribe(new DefaultLoyoSubscriber<SaleProductEdit>() {
            @Override
            public void onNext(SaleProductEdit saleProductEdit) {
                mPersenter.editProductSuccess(data);
            }
        });
    }
}