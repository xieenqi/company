package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.home.bean.HttpAchieves;
import com.loyo.oa.v2.activity.home.bean.HttpBulking;
import com.loyo.oa.v2.activity.home.bean.HttpProcess;
import com.loyo.oa.v2.activity.home.bean.HttpSalechance;
import com.loyo.oa.v2.activity.home.bean.HttpStatistics;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * 统计相关 接口
 * Created by xeq on 16/6/17.
 */
public interface IStatistics {
    /**
     * 第一次获取 【销售统计全部】数据
     *
     * @param map
     * @param callback statistics/query?startAt=1465747200&endAt=1466352000
     */
    @GET("/statistics/query")
    void getNoticeList(@QueryMap HashMap<String, Object> map, Callback<HttpStatistics> callback);

    /**
     * 获取 【过程统计】 的 今日 本周 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/process/query")
    void getProcessList(@QueryMap HashMap<String, Object> map, Callback<List<HttpProcess>> callback);

    /**
     * 获取 【增量统计】 的 今日 本周 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/bulking/query")
    void getBulkingList(@QueryMap HashMap<String, Object> map, Callback<List<HttpBulking>> callback);

    /**
     * 获取 【业绩目标】 的 本周 本月 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/achieve/query")
    void getAchievesList(@QueryMap HashMap<String, Object> map, Callback<List<HttpAchieves>> callback);

    /**
     * 获取 【销售漏斗】 的 本周 本月 数据
     *
     * @param map
     * @param callback
     */
    @GET("/statistics/salechance/query")
    void getFunnelList(@QueryMap HashMap<String, Object> map, Callback<List<HttpSalechance>> callback);
}
