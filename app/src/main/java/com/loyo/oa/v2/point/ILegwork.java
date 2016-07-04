package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.activityui.signin.bean.PaginationLegWork;
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
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/22.
 */
public interface ILegwork {

    @GET("/visit/byuserId/")
    void getLegworks(@QueryMap HashMap<String, Object> map, Callback<PaginationX<LegWork>> callback);

    //团队拜访
    @GET("/statistics/visit/team")
    void getTeamLegworks(@QueryMap HashMap<String, Object> map, Callback<PaginationLegWork> callback);

    @GET("/visit/bycustId")
    void getCustomerLegworks(@QueryMap HashMap<String, Object> map, Callback<PaginationX<LegWork>> callback);

    @GET("/visit/{id}")
    void getLegworkInfo(@Path("id") String id, Callback<LegWork> callback);

    @POST("/visit/")
    void createLegwork(@Body HashMap<String, Object> map, Callback<PaginationX<LegWork>> callback);


}
