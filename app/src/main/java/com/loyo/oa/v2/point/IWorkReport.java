package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.beans.WorkReportTpl;
import com.loyo.oa.v2.beans.WorkreportLastUsers;
import com.loyo.oa.v2.common.FinalVariables;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by pj on 15/5/11.
 */
public interface IWorkReport {

    @GET("/discussions?bizType=1")
    void getDiscussions(@Query("bizId") String id, retrofit.Callback<PaginationX<Discussion>> cb);

    @POST("/discussions")
    void createDiscussion(
            @Body HashMap<String, Object> body,
            retrofit.Callback<Discussion> cb);

    @GET(FinalVariables.workreports + "template/{id}")
    void getTpl(@Path("id") String id, Callback<WorkReportTpl> cb);

    @DELETE(FinalVariables.workreports + "template/{id}")
    void deleteTpl(@Path("id") String id, Callback<WorkReportTpl> cb);

    @GET(FinalVariables.workreports + "lastusers")
    void getLastUsers(Callback<WorkreportLastUsers> cb);

    /**
     * 根据ID获取报告详情
     * @param id
     * @param cb
     */
    @GET("/wreport/{id}")
    void get(@Path("id") String id, Callback<WorkReport> cb);

    /**
     * 根据筛选条件获取报告列表
     * @param map
     * @param cb
     */
    @GET("/wreport/")
    void getWorkReports(@QueryMap HashMap<String ,Object> map,Callback<PaginationX<WorkReport>> cb);

    /**
     * 点评报告
     * @param id
     * @param map
     * @param cb
     */
    @PUT("/wreport/{id}/review")
    void reviewWorkReport(@Path("id") String id,@Body HashMap<String ,Object> map,Callback<WorkReport> cb);

    /**
     * 创建报告
     * @param map
     * @param cb
     */
    @POST("/wreport/")
    void createWorkReport(@Body HashMap<String ,Object> map,Callback<WorkReport> cb);

    /**
     * 删除报告
     * @param id
     * @param cb
     */
    @DELETE("/wreport/{id}")
    void deleteWorkReport(@Path("id") String  id,Callback<WorkReport> cb);

    @PUT("/wreport/{id}")
    void updateWorkReport(@Path("id") String id,@Body HashMap<String ,Object> map,Callback<WorkReport> cb);
}