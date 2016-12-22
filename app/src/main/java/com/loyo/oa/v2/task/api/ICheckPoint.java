package com.loyo.oa.v2.task.api;

import com.loyo.oa.v2.beans.TaskCheckPoint;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * com.loyo.oa.v2.point
 * 描述 :子任务网络操作请求接口
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
public interface ICheckPoint {
    @DELETE("/task/{id}/checkitem/{checkPointId}")
    Observable<TaskCheckPoint> deleteChildTask(@Path("id") String id, @Path("checkPointId") String checkPointId);

    @PUT("/task/{id}/checkitem/{checkPointId}")
    Observable<TaskCheckPoint> updateChildTask(@Path("id") String id, @Path("checkPointId") String checkPointId, @Body HashMap<String, Object> body);

    @POST("/task/{id}/checkitem")
    Observable<TaskCheckPoint> createChildTask(@Path("id") String id, @Body HashMap<String, Object> body);

    /**
     * xnq 跟新 编辑 子任务
     * @param id
     * @param checkPointId
     * @param body
     */
    @PUT("/task/{id}/checkitem/{cid}")
    Observable<TaskCheckPoint> updateChildTaskInfo(@Path("id") String id, @Path("cid") String checkPointId, @Body HashMap<String, Object> body);

    /**
     * 删除 子任务 xnq
     * @param id
     * @param checkPointId
     */
    @DELETE("/task/{id}/checkitem/{cid}")
    Observable<TaskCheckPoint> deleteChildTaskInfo(@Path("id") String id, @Path("cid") String checkPointId);
}
