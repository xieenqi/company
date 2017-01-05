package com.loyo.oa.v2.activityui.product.api;

import com.loyo.oa.v2.activityui.product.model.ProductClassifyModel;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorItem;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.Config_project;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public static Observable<PaginationX<ProductListModel>> getProductList(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getSerachProduct(params)
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

    //获取产品分类
    public static Observable<List<ClassifySeletorItem>> getProductClassify() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IProduct.class)
                        .getProductClassify()
                        .compose(RetrofitAdapterFactory.<List<ClassifySeletorItem>>applySchedulers());
    }

}
