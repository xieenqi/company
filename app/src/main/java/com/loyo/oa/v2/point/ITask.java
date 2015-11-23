package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskTpl;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.RCallback;

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

public interface ITask {

    @GET("/discussions?bizType=2")
    void getDiscussions(@Query("bizId") String taskId, retrofit.Callback<PaginationX<Discussion>> cb);

    @POST("/discussions")
    void createDiscussion(@Body HashMap<String, Object> body, retrofit.Callback<Discussion> cb);

    @GET("/task/{Id}")
    void getTask(@Path("Id") String Id, retrofit.Callback<Task> cb);

    @DELETE("/task/{Id}")
    void deleteTask(@Path("Id") String Id, retrofit.Callback<Task> cb);

    @GET(FinalVariables.tasks + "template/{id}")
    void getTpl(@Path("id") String id, Callback<TaskTpl> cb);

    @DELETE(FinalVariables.tasks + "template/{id}")
    void deleteTpl(@Path("id") String id, Callback<TaskTpl> cb);

    @GET("/task/query")
    Observable<PaginationX<Task>> getList(@QueryMap HashMap<String, Object> body);

    @GET("/task/query")
    void  getTasks(@QueryMap HashMap<String, Object> body,Callback<PaginationX<Task>> callback);

//    @POST("/task")
//    Observable<Task> create(@Body HashMap<String, Object> body);

    @POST("/task")
    void create(@Body HashMap<String, Object> body, retrofit.Callback<Task> cb);

    @PUT("/task/{Id}")
    Observable<Task> update(@Path("Id") String Id,@Body HashMap<String, Object> body);

    @PUT("/task/{id}/finished")
    void commitTask(@Path("id") String id ,Callback<Task> callback);

    @PUT("/task/{id}/review")
    void verifyTask(@Path("id") String id,@Body HashMap<String ,Object> map,RCallback<Task> callback);

//       @Param bizId query int true "业务 Id"
//      @Param bizType query int true "业务类型，1:工作报告, 2:任务, 3:日程, 4:外勤, 5: 项目"
}
