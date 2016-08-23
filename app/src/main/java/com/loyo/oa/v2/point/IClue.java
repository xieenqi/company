package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.clue.bean.SourcesData;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * 线索相关接口 相关
 * Created by xeq on 16/8/22.
 */
public interface IClue {
    /**
     * 获取 线索来源
     *
     * @param callback
     */
    @GET("/salesleads/sources")
    void getSource(Callback<SourcesData> callback);

    /**
     * 新建线索 表单传输
     *
     * @param body
     * @param callback
     */
    @POST("/salesleads")
    void addClue(@Body Map<String, Object> body, Callback<Object> callback);
}
