package com.loyo.oa.v2.activityui.sale.presenter;

import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.contract.AddIntentionProductContract;
import com.loyo.oa.v2.activityui.sale.model.AddIntentionProductModelImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/11/30
 */

public class AddIntentionProductPresenterImpl implements AddIntentionProductContract.Presenter {

    private AddIntentionProductContract.View mView;
    private AddIntentionProductContract.Model model;

    public AddIntentionProductPresenterImpl(AddIntentionProductContract.View mView) {
        this.mView = mView;
        model = new AddIntentionProductModelImpl(this);
    }

    @Override
    public void getPageData(Object... pag) {

    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    @Override
    public void getProduct() {
        model.getProductData();
    }

    @Override
    public void setProduct(ArrayList<Product> products) {
        mView.setProductInfo(products);
    }

    @Override
    public void closePage() {
        mView.closePageUI();
    }

    @Override
    public void addProduct(HashMap<String, Object> map, SaleIntentionalProduct data) {
        model.addProductSend(map, data);
    }

    @Override
    public void addProductSuccess(SaleIntentionalProduct data) {
        mView.addProductSuccessUI(data);
    }

    @Override
    public void editProduct(HashMap<String, Object> map, SaleIntentionalProduct data, String saleId) {
        model.editProductSend(map, data, saleId);
    }

    @Override
    public void editProductSuccess(SaleIntentionalProduct data) {
        mView.editProductSuccessUI(data);
    }
}