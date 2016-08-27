package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.TrackLog;
import com.loyo.oa.v2.beans.TrackRule;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface ITrackLog {

    @POST("/tracklog")
    void createTrackLog(@Body HashMap<String, String> body, retrofit.Callback<TrackLog> cb);

    @GET("/trackrule")
    void getTrackRule(retrofit.Callback<TrackRule> cb);

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
