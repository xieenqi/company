package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 描述 :通知交互接口
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
public interface INotice
{
    /**
     * 分页获取通知列表
     * @param map
     * @param callback
     */
    @GET("/bulletin/query")
    void getNoticeList(@QueryMap HashMap<String,Object> map, Callback<PaginationX<Bulletin>> callback);

    /**
     * 发布通知
     * @param body
     * @param callback
     */
    @POST("/bulletin")
    void publishNotice(@Body HashMap<String, Object> body,Callback<Bulletin> callback);

    /**
     * 根据id查看通知详情
     * @param id
     * @param callback
     */
    @GET("/bulletin/{id}")
    void examineNoticeById(@Path("id") int id,Callback<Bulletin> callback);

}
