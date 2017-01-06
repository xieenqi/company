package com.loyo.oa.v2.activityui.project.api;

import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.activityui.project.ProjectAddActivity;
import com.loyo.oa.v2.beans.Pagination;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;

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
 * 项目与服务器的接口
 * Created by jie on 16/12/21.
 */

public interface IProject {

    @GET("/project/query")
    Observable<PaginationX<Project>> getProjects(@QueryMap HashMap<String, Object> map);

    @GET("/project/{id}")
    Observable<HttpProject> getProjectById(@Path("id") String id, @Query("key") String key);

    @PUT("/project/{id}")
    Observable<Project> Update(@Path("id") String id, @Body ProjectAddActivity.ProjectTransObj body);


    //1:工作报告, 2:任务, 12:快捷审批
    @GET("/project/{id}/records/{bizType}")
    Observable<Pagination> getProjectSubs(@Path("id") String id, @Path("bizType") int bizType, @QueryMap HashMap<String, Object> map);

    //1:工作报告, 2:任务, 12:快捷审批
    @GET("/project/{id}/records/{bizType}/mobile/simplify")
    Observable<Pagination> getProjectNewSubs(@Path("id") String id, @Path("bizType") int bizType, @QueryMap HashMap<String, Object> map);


    @POST("/project")
    Observable<Project> Create(@Body ProjectAddActivity.ProjectTransObj body);

    @DELETE("/project/{id}")
    Observable<Project> deleteProject(@Path("id") String id);

    @PUT("/project/{id}/status/{status}")
    Observable<Project> UpdateStatus(@Path("id") String id, @Path("status") int status);
}
