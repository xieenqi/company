package com.loyo.oa.v2.activityui.product.api;

import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.product.model.ProductCustomField;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorItem;
import com.loyo.oa.v2.network.model.BaseResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/30.
 */

public interface IProduct {

    /**
     * 获取产品动态字段
     */
    @GET("/properties")
    Observable<ArrayList<ProductCustomField>> getProductDynm(@QueryMap HashMap<String, Object> map);

    /**
     * 产品列表
     */
    @GET("/product")
    Observable<BaseResponse<ProductListModel>> getSerachProduct( @QueryMap HashMap<String, Object> map);

    /**
     * 产品详情
     */
    @GET("/product/web/{id}")
    Observable<BaseResponse<ProductDetails>> getProductDetails(@Path("id") String categoryId);

    /**
     * 产品分类数据
     */
    @GET("/product/category/list")
    Observable<BaseResponse<List<ClassifySeletorItem>>> getProductClassify();
}
