package com.loyo.oa.v2.task.api;

import com.loyo.oa.v2.activityui.tasks.bean.TaskTpl;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.common.FinalVariables;

import java.util.HashMap;

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

    /*获取任务信息*/
    @GET("/task/{Id}")
    Observable<Task> getTask(@Path("Id") String Id, @Query("key") String key);

    @DELETE("/task/{Id}")
    Observable<Task> deleteTask(@Path("Id") String Id);

    @GET(FinalVariables.tasks + "template/{id}")
    Observable<TaskTpl> getTpl(@Path("id") String id);

    @DELETE(FinalVariables.tasks + "template/{id}")
    Observable<TaskTpl> deleteTpl(@Path("id") String id);

    @GET("/task/query")
    Observable<PaginationX<Task>> getList(@QueryMap HashMap<String, Object> body);

    /**
     * 客户任务列表(v2.2精简接口)
     * */
    @GET("/task/query/mobile/simplify")
    Observable<PaginationX<TaskRecord>> getListData(@QueryMap HashMap<String, Object> body);

    /**
     * 获取任务列表数据
     * */
    @GET("/task/query")
    Observable<PaginationX<Task>> getTasks(@QueryMap HashMap<String, Object> body);

    /**
     * 获取任务列表数据(v2.2 精简接口)
     * */
    @GET("/task/query/mobile/simplify")
    Observable<PaginationX<TaskRecord>> getTasksData(@QueryMap HashMap<String, Object> body);

    /*创建任务*/
    @POST("/task")
    Observable<Task> create(@Body HashMap<String, Object> body);

    /*更新任务 弃用*/
    @PUT("/task/{Id}")
    Observable<Task> update(@Path("Id") String Id, @Body HashMap<String, Object> body);

    /*更新任务*/
    @PUT("/task/{Id}")
    Observable<Task> updateTask(@Path("Id") String Id, @Body HashMap<String, Object> body);

    /*修改参与人*/
    @PUT("/task/{Id}")
    Observable<Task> updateJioner(@Path("Id") String Id, @Body HashMap<String, Object> body);

    /*任务完成*/
    @PUT("/task/{id}/finished")
    Observable<Task> commitTask(@Path("id") String id);

    /*任务审核*/
    @PUT("/task/{id}/review")
    Observable<Task> verifyTask(@Path("id") String id, @Body HashMap<String, Object> map);

    /*更新子任务状态*/
    @PUT("/task/{id}/checkitemstatus/{cid}")
    Observable<Task> updatesTask(@Path("id") String id, @Path("cid") String cid, @Body HashMap<String, Object> map);


//       @Param bizId query int true "业务 Id"
//      @Param bizType query int true "业务类型，1:工作报告, 2:任务, 3:日程, 4:外勤, 5: 项目"
}
