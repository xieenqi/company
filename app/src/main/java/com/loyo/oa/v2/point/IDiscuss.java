package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 描述 :讨论服务端接口
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public interface IDiscuss {

    @GET("/discussion/query")
    void getDiscussions(@QueryMap HashMap<String, Object> map, Callback<PaginationX<Discussion>> callback);

    @POST("/discussion/")
    void createDiscussion(@Body HashMap<String, Object> map, Callback<Discussion> callback);

    @GET("/discussion/count")
    void createDiscussion(@Query("attachmentUUId") String attachmentUUId, Callback<PaginationX<Discussion>> callback);
}
