package com.loyo.oa.v2.order.api;

import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.activityui.order.bean.ProcessItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 订单模块接口相关
 * Created by xeq on 16/8/3.
 */
public interface IOrder {


    /**
     * 获取 我的订单列表
     */
    @GET("/order/self")
    Observable<PaginationX<OrderListItem>> getOrderMyList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 团队订单列表
     */
    @GET("/order/team")
    Observable<PaginationX<OrderListItem>> getOrderTeamList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 订单详情 order/57a0692d35d860eca276d964
     */
    @GET("/order/{id}")
    Observable<OrderDetail> getSaleDetails(@Path("id") String id);

    /**
     * 创建订单
     */
    @POST("/order")
    Observable<BaseResponse<OrderAdd>> addOrder(@Body HashMap<String, Object> map);

    /**
     * 编辑订单
     * */
    @PUT("/order/{id}")
    Observable<BaseResponse<OrderAdd>> editOrder(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 创建回款计划
     */
    @POST("/order/plan")
    Observable<EstimatePlanAdd> addPlanEstimate(@Body HashMap<String, Object> map);

    /**
     * 获取 回款计划列表
     */
    @GET("/order/plan/")
    Observable<ArrayList<PlanEstimateList>> getPlanEstimateList(@QueryMap HashMap<String, Object> map);

    /**
     * 删除 回款计划
     */
    @DELETE("/order/plan/{id}")
    Observable<EstimatePlanAdd> deletePlanEsstimateList(@Path("id") String id);

    /**
     * 编辑 回款计划
     */
    @PUT("/order/plan/{id}")
    Observable<EstimatePlanAdd> editPlanEsstimate(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 意外终止订单
     */
    @PUT("/order/unexpecte/{id}")
    Observable<Object> terminationOrder(@Path("id") String id);

    /**
     * 意外终止订单
     */
    @PUT("/order/unexpecte/{id}")
    Observable<Object> terminationOrderWithProcess(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 删除订单
     */
    @DELETE("/order/{id}")
    Observable<Object> deleteOrder(@Path("id") String id);

    /**
     * 创建 回款记录
     * */
    @POST("/order/pay")
    Observable<EstimateAdd> addPayEstimate(@Body HashMap<String, Object> map);

    /**
     * 获取 回款记录列表
     * */
    @GET("/order/record/{id}")
    Observable<EstimateList> getPayEstimate(@Path("id") String id);

    /**
     * 删除 回款记录
     * */
    @DELETE("/order/pay/{id}")
    Observable<EstimateAdd> deletePayEstimate(@Path("id") String id);

    /**
     * 编辑 回款记录
     * */
    @PUT("/order/pay/{id}")
    Observable<EstimateAdd> editPayEstimate(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 获取 终止是否需要审批流程
     * */
    @GET("/order/TODO:") //TODO:
    Observable<BaseResponse<Boolean>> getTerminateProcessConfig();

    /**
     * 获取 终止审批流程列表
     * */
    @GET("/order/TODO:") // TODO:
    Observable<BaseResponse<ArrayList<ProcessItem>>> getTerminateProcessList();

}
