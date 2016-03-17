package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.discuss.HttpDiscussDet;
import com.loyo.oa.v2.activity.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activity.discuss.HttpMyDiscussItem;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 讨论模块 的接口
 * 时间 : 15/8/28.
 */
public interface MyDiscuss {
    /**
     * 分页获取【我的讨论】列表
     *
     * @param map
     * @param callback
     */
    @GET("/discussion/subject/query")
    void getDisscussList(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpDiscussItem>> callback);

    /**
     * 分页获取【@我讨论】列表
     *
     * @param callback
     */
    @GET("/discussion/at/query")
    void getMyDisscussList(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpMyDiscussItem>> callback);


    @GET("/discussion/query")
    void getDiscussDetail(@QueryMap HashMap<String, Object> map, Callback<PaginationX<HttpDiscussDet>> callback);

}
