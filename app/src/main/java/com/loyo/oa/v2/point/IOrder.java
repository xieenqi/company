package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**订单模块接口相关
 * Created by xeq on 16/8/3.
 */
public interface IOrder {
    /**
     * 获取 订单详情 order/57a0692d35d860eca276d964
     */
    @GET("/order/{id}")
    void getSaleDetails(@Path("id") String id, Callback<SaleDetails> callback);
}
