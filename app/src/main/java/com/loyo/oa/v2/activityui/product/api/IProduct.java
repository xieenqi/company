package com.loyo.oa.v2.activityui.product.api;

import com.loyo.oa.v2.activityui.product.model.ProductDynmModel;
import java.util.HashMap;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/30.
 */

public interface IProduct {

    /**
     * 获取产品动态字段
     *   http://112.74.66.99:8070/api/v2/properties?bizType=102&isAdjust=1
     */
    @GET("/properties")
    Observable<ProductDynmModel> getProductDynm(@QueryMap HashMap<String, Object> map);

}
