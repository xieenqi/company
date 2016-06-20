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

    /**
     * 获取 过程统计 的 今日 本周 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/process/query")
    void geProcessList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);

    /**
     * 获取 增量统计 的 今日 本周 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/bulking/query")
    void geBulkingList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);

    /**
     * 获取 业绩目标 的 本周 本月 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/achieve/query")
    void geAchievesList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);

    /**
     * 获取 销售漏斗 的 本周 本月 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/salechance/query")
    void geFunnelList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);
}
