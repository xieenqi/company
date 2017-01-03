package com.loyo.oa.v2.activityui.product.api;

import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import java.util.ArrayList;
import java.util.HashMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/30.
 */

public class ProductService {

    //获取产品动态字段
    public static Observable<ArrayList<ProductDynmModel>> getProductDynm(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getProductDynm(params)
                        .compose(RetrofitAdapterFactory.<ArrayList<ProductDynmModel>>compatApplySyncSchedulers());
    }

    //获取产品列表
    public static Observable<PaginationX<ProductListModel>> getProductList(String id,HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getSerachProduct(id,params)
                        .compose(RetrofitAdapterFactory.<PaginationX<ProductListModel>>applySchedulers());
    }

    //获取产品详情
    public static Observable<ProductDetails> getProductDetails(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getProductDetails(id)
                        .compose(RetrofitAdapterFactory.<ProductDetails>applySchedulers());
    }

}
