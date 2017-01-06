package com.loyo.oa.tracklog.api;

//import com.loyo.oa.v2.beans.TrackLocationRule;

import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

public interface ITrackLog {
    /***
     * 最新上传轨迹 经纬度 时间
     * @param body
     */
    @POST("/api/v1/track_logs ")
    Observable<TrackLog> newUploadTrack(@Body HashMap<String, Object> body);

    @GET("/trackrule")
    Observable<TrackRule> getTrackRule();

    @POST("/tracklog/multiple")
    Observable<Object> uploadTrackLogs(@Body HashMap<String, Object> tracklogs);

    /**
     * 轨迹用户在线
     */
    @GET("/tracklog/record")
    Observable<Object> getUserOneLine();
}
