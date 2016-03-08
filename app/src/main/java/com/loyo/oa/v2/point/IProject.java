package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.project.ProjectAddActivity;
import com.loyo.oa.v2.activity.project.HttpProject;
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
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 描述 :项目与服务端交互类
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public interface IProject {

    @GET("/project/query")
    void getProjects(@QueryMap HashMap<String, Object> map, Callback<PaginationX<Project>> callback);

    @GET("/project/{id}")
    void getProjectById(@Path("id") String id, Callback<HttpProject> callback);

    @PUT("/project/{id}")
    void Update(@Path("id") String id, @Body ProjectAddActivity.ProjectTransObj body, Callback<Project> callback);

    @GET("/project/{id}/records/{bizType}")//1:工作报告, 2:任务, 12:快捷审批
    void getProjectSubs(@Path("id") String id, @Path("bizType") int bizType, @QueryMap HashMap<String, Object> map, Callback<Pagination> callback);

    @POST("/project")
    void Create(@Body ProjectAddActivity.ProjectTransObj body,
                retrofit.Callback<Project> cb);

    @DELETE("/project/{id}")
    void deleteProject(@Path("id") String id, Callback<Project> callback);

    @PUT("/project/{id}/status/{status}")
    void UpdateStatus(@Path("id") String id, @Path("status") int status, Callback<Project> callback);
}
