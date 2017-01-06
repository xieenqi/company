package com.loyo.oa.v2.activityui.work.api;

import com.loyo.oa.v2.activityui.work.bean.HttpDefaultComment;
import com.loyo.oa.v2.activityui.work.bean.WorkReportDyn;
import com.loyo.oa.v2.activityui.work.bean.WorkReportTpl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.FinalVariables;

import java.util.ArrayList;
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
import rx.Observable;

/**
 * 工作报告网络接口
 * Created by jie on 16/12/21.
 */

public interface IWorkReport {


    /**
     * 获取动态工作报告
     */
    @GET("/statistics/process/number")
    Observable<ArrayList<WorkReportDyn>> getDynamic(@QueryMap HashMap<String, Object> map);

    /**
     * 根据ID获取报告详情
     */
    @GET("/wreport/{id}")
    Observable<WorkReport> getWorkReportDetail(@Path("id") String id, @Query("key") String key);

    /**
     * 根据筛选条件获取报告列表
     */
    @GET("/wreport/")
    Observable<PaginationX<WorkReport>> getWorkReports(@QueryMap HashMap<String, Object> map);

    /**
     * 根据筛选条件获取报告列表(v2.2 精简接口)
     */
    @GET("/wreport/mobile/simplify")
    Observable<PaginationX<WorkReportRecord>> getWorkReportsData(@QueryMap HashMap<String, Object> map);

    /**
     * 点评报告
     */
    @PUT("/wreport/{id}/review")
    Observable<WorkReport> reviewWorkReport(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 创建报告
     */
    @POST("/wreport/")
    Observable<WorkReport> createWorkReport(@Body HashMap<String, Object> map);

    /**
     * 删除报告
     */
    @DELETE("/wreport/{id}")
    Observable<WorkReport> deleteWorkReport(@Path("id") String id);

    @PUT("/wreport/{id}")
    Observable<WorkReport> updateWorkReport(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 获取默认的点评人
     */
    @GET("/wreport/latest")
    Observable<HttpDefaultComment> defaultComment();
}
