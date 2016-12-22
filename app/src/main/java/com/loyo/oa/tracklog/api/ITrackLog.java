package com.loyo.oa.tracklog.api;

//import com.loyo.oa.v2.beans.TrackLocationRule;

import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface ITrackLog {
    /***
     * 最新上传轨迹 经纬度 时间
     * @param body
     * @param cb
     */
    @POST("/api/v1/track_logs ")
    void newUploadTrack(@Body HashMap<String, Object> body, Callback<TrackLog> cb);

    @GET("/trackrule")
    void getTrackRule(retrofit.Callback<TrackRule> cb);

//    @GET("/trackrule")
//    void getTrackLocationRule(retrofit.Callback<TrackLocationRule> cb);

    @POST("/tracklog/multiple")
    void uploadTrackLogs(@Body HashMap<String, Object> tracklogs, Callback<Object> callback);

    /**
     * 轨迹用户在线
     *
     * @param cb
     */
    @GET("/tracklog/record")
    void getUserOneLine(Callback<Object> cb);
}
