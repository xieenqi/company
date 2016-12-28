package com.loyo.oa.v2.activityui.discuss.api;

import com.loyo.oa.v2.activityui.discuss.HttpDiscussDet;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.HttpMyDiscussItem;
import com.loyo.oa.v2.activityui.discuss.bean.Discussion;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 讨论模块接口
 * Created by jie on 16/12/21.
 */

public interface IDiscuss {

//    讨论
    /**
     * 分页获取【我的讨论】列表
     */
    @GET("/discussion/subject/query")
    Observable<PaginationX<HttpDiscussItem>> getDisscussList(@QueryMap HashMap<String, Object> map);

    /**
     * 分页获取【@我讨论】列表
     */
    @GET("/discussion/at/query")
    Observable<PaginationX<HttpMyDiscussItem>> getMyDisscussList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取讨论详细
     */
    @GET("/discussion/query")
    Observable<PaginationX<HttpDiscussDet>> getDiscussDetail(@QueryMap HashMap<String, Object> map);
    /**
     * 更新@红点
     */
    @PUT("/discussion/read")
    Observable<Object> updateReadDot(@QueryMap HashMap<String, Object> map);

//    我的讨论
    @GET("/discussion/query")
    Observable<PaginationX<Discussion>> getDiscussions(@QueryMap HashMap<String, Object> map);

    @POST("/discussion/")
    Observable<Discussion> createDiscussion(@Body HashMap<String, Object> map);

    @GET("/discussion/count")
    Observable<PaginationX<Discussion>> createDiscussion(@Query("attachmentUUId") String attachmentUUId);



}
