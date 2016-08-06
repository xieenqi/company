package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.bean.OrderList;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
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

    /**
     * 创建订单
     */
    @POST("/order")
    void addOrder(@Body HashMap<String, Object> map, Callback<OrderAdd> callback);

    /**
     * 创建回款计划
     */
    @POST("/order/plan")
    void addPlanEstimate(@Body HashMap<String, Object> map, Callback<EstimatePlanAdd> callback);

    /**
     * 获取 回款计划列表
     */
    @GET("/order/plan/")
    void getPlanEstimateList(@QueryMap HashMap<String, Object> map, Callback<ArrayList<PlanEstimateList>> callback);

    /**
     * 删除 回款计划
     */
    @DELETE("/order/plan/{id}")
    void deletePlanEsstimateList(@Path("id") String id, Callback<EstimatePlanAdd> callback);

    /**
     * 编辑 回款计划
     */
    @PUT("/order/plan/{id}")
    void editPlanEsstimate(@Path("id") String id, @Body HashMap<String, Object> map, Callback<EstimatePlanAdd> callback);

    /**
     * 意外终止订单
     */
    @PUT("/order/unexpecte/{id}")
    void terminationOrder(@Path("id") String id, Callback<Object> callback);

    /**
     * 删除订单
     */
    @DELETE("/order/{id}")
    void deleteOrder(@Path("id") String id, Callback<Object> callback);

    /**
     * 创建 回款记录
     * */
    @POST("/order/pay")
    void addPayEstimate(@Body HashMap<String,Object> map,Callback<EstimateAdd> callback);

    /**
     * 获取 回款记录列表
     * */
    @GET("/order/record/{id}")
    void getPayEstimate(@Path("id") String id,Callback<ArrayList<EstimateAdd>> callback);

    /**
     * 删除 回款记录
     * */
    @DELETE("/order/pay/{id}")
    void deletePayEstimate(@Path("id") String id,Callback<EstimateAdd> callback);

    /**
     * 编辑 回款记录
     * */
    @PUT("/order/pay/{id}")
    void editPayEstimate(@Path("id") String id,@Body HashMap<String,Object> map, Callback<EstimateAdd> callback);

}
