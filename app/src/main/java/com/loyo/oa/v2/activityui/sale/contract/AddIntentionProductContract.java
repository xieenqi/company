package com.loyo.oa.v2.activityui.sale.contract;

import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 16/11/30.
 */

public interface AddIntentionProductContract {

    interface View extends BaseView {
        void setProductInfo(ArrayList<Product> products);

        void closePageUI();

        void addProductSuccessUI(SaleIntentionalProduct data);

        void editProductSuccessUI(SaleIntentionalProduct data);
    }

    interface Presenter extends BasePersenter {
        void getProduct();

        void setProduct(ArrayList<Product> products);

        void closePage();

        void addProduct(HashMap<String, Object> map, SaleIntentionalProduct data);

        void addProductSuccess(SaleIntentionalProduct data);

        void editProduct(HashMap<String, Object> map, SaleIntentionalProduct data,String saleId);

        void editProductSuccess(SaleIntentionalProduct data);
    }

    interface Model {
        void getProductData();

        void addProductSend(HashMap<String, Object> map, SaleIntentionalProduct data);

        void editProductSend(HashMap<String, Object> map, SaleIntentionalProduct data,String saleId);
    }


}