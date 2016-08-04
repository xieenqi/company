package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.bean.OrderList;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 订单模块接口相关
 * Created by xeq on 16/8/3.
 */
public interface IOrder {


    /**
     * 获取 我的订单列表
     */
    @GET("/order/self")
    void getOrderMyList(@QueryMap HashMap<String, Object> map, Callback<OrderList> callback);

    /**
     * 获取 团队订单列表
     */
    @GET("/order/team")
    void getOrderTeamList(@QueryMap HashMap<String, Object> map, Callback<OrderList> callback);

    /**
     * 获取 订单详情 order/57a0692d35d860eca276d964
     */
    @GET("/order/{id}")
    void getSaleDetails(@Path("id") String id, Callback<OrderDetail> callback);

    @POST("/order")
    void addOrder(@Body HashMap<String,Object> map,Callback<OrderAdd> callback);


}
