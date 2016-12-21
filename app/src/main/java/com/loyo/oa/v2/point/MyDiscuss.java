package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.discuss.HttpDiscussDet;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.HttpMyDiscussItem;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 讨论模块 的接口
 * 时间 : 15/8/28.
 */
public interface MyDiscuss {
//    /**
//     * 分页获取【我的讨论】列表
//     *
//     * @param map
//     * @param callback
//     */
//    @GET("/discussion/subject/query")
//    void getDisscussList(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpDiscussItem>> callback);
//
//    /**
//     * 分页获取【@我讨论】列表
//     *
//     * @param callback
//     */
//    @GET("/discussion/at/query")
//    void getMyDisscussList(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpMyDiscussItem>> callback);
//
//    /**
//     * 获取讨论详细
//     *
//     * @param map
//     * @param callback
//     */
//    @GET("/discussion/query")
//    void getDiscussDetail(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpDiscussDet>> callback);
//
//    //    http://192.168.31.136:8050/api/v2/discussion/read?summaryId=56ea106debe07f6b21000001
////    查看以后修改view状态的接口
//
//    /**
//     * 更新@红点
//     * @param map
//     * @param cb
//     */
//    @PUT("/discussion/read")
//    void updateReadDot(@QueryMap HashMap<String, Object> map, retrofit.Callback<Object> cb);
}
