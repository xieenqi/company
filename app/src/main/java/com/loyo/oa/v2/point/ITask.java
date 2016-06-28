package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.ui.activity.tasks.bean.TaskTpl;
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

//    @GET("/discussions?bizType=2")
//    void getDiscussions(@Query("bizId") String taskId, retrofit.Callback<PaginationX<Discussion>> cb);
//
//    @POST("/discussions")
//    void createDiscussion(@Body HashMap<String, Object> body, retrofit.Callback<Discussion> cb);

    /*获取任务信息*/
    @GET("/task/{Id}")
    void getTask(@Path("Id") String Id, @Query("key") String key, retrofit.Callback<Task> cb);

    @DELETE("/task/{Id}")
    void deleteTask(@Path("Id") String Id, retrofit.Callback<Task> cb);

    @GET(FinalVariables.tasks + "template/{id}")
    void getTpl(@Path("id") String id, Callback<TaskTpl> cb);

    @DELETE(FinalVariables.tasks + "template/{id}")
    void deleteTpl(@Path("id") String id, Callback<TaskTpl> cb);

    @GET("/task/query")
    Observable<PaginationX<Task>> getList(@QueryMap HashMap<String, Object> body);

    /**
     * 获取任务列表数据
     * */
    @GET("/task/query")
    void getTasks(@QueryMap HashMap<String, Object> body, Callback<PaginationX<Task>> callback);

    /**
     * 获取任务列表数据(v2.2 精简接口)
     * */
    @GET("/task/query/mobile/simplify")
    void getTasksData(@QueryMap HashMap<String, Object> body, Callback<PaginationX<TaskRecord>> callback);

    /*创建任务*/
    @POST("/task")
    void create(@Body HashMap<String, Object> body, retrofit.Callback<Task> cb);

    /*更新任务*/
    @PUT("/task/{Id}")
    Observable<Task> update(@Path("Id") String Id, @Body HashMap<String, Object> body);

    /*修改参与人*/
    @PUT("/task/{Id}")
    void updateJioner(@Path("Id") String Id, @Body HashMap<String, Object> body, RCallback<Task> callback);

    /*任务完成*/
    @PUT("/task/{id}/finished")
    void commitTask(@Path("id") String id, Callback<Task> callback);

    /*任务审核*/
    @PUT("/task/{id}/review")
    void verifyTask(@Path("id") String id, @Body HashMap<String, Object> map, RCallback<Task> callback);

    /*更新子任务状态*/
    @PUT("/task/{id}/checkitemstatus/{cid}")
    void updatesTask(@Path("id") String id, @Path("cid") String cid, @Body HashMap<String, Object> map, RCallback<Task> callback);


//       @Param bizId query int true "业务 Id"
//      @Param bizType query int true "业务类型，1:工作报告, 2:任务, 3:日程, 4:外勤, 5: 项目"
}
