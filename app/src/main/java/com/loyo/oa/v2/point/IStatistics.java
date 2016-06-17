package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.home.bean.HttpStatistics;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * 统计相关 接口
 * Created by xeq on 16/6/17.
 */
public interface IStatistics {
    /**
     * 第一次获取 销售统计全部数据
     *
     * @param map
     * @param callback statistics/query?startAt=1465747200&endAt=1466352000
     */
    @GET("/statistics/query")
    void getNoticeList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);
}
