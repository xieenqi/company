package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.TaskCheckPoint;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * com.loyo.oa.v2.point
 * 描述 :子任务网络操作请求接口
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
public interface ICheckPoint {
    @DELETE("/task/{id}/checkitem/{checkPointId}")
    void deleteChildTask(@Path("id") String id, @Path("checkPointId") String checkPointId, retrofit.Callback<TaskCheckPoint> cb);

    @PUT("/task/{id}/checkitem/{checkPointId}")
    void updateChildTask(@Path("id") String id, @Path("checkPointId") String checkPointId, @Body HashMap<String, Object> body, retrofit.Callback<TaskCheckPoint> cb);

    @POST("/task/{id}/checkitem")
    void createChildTask(@Path("id") String id, @Body HashMap<String, Object> body, retrofit.Callback<TaskCheckPoint> cb);
}
