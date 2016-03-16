package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 通知模块 的接口
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
    void getDisscussList(@QueryMap HashMap<String, Object> map, Callback<PaginationX<Bulletin>> callback);

//    /**
//     * 发布通知
//     * @param body
//     * @param callback
//     */
//    @POST("/bulletin")
//    void publishNotice(@Body HashMap<String, Object> body, Callback<Bulletin> callback);
//
//    /**
//     * 根据id查看通知详情
//     * @param id
//     * @param callback
//     */
//    @GET("/bulletin/{id}")
//    void examineNoticeById(@Path("id") int id, Callback<Bulletin> callback);

}
