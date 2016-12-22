package com.loyo.oa.v2.activityui.home.api;

import com.loyo.oa.v2.activityui.home.bean.HttpAchieves;
import com.loyo.oa.v2.activityui.home.bean.HttpBulking;
import com.loyo.oa.v2.activityui.home.bean.HttpProcess;
import com.loyo.oa.v2.activityui.home.bean.HttpSalechance;
import com.loyo.oa.v2.activityui.home.bean.HttpStatistics;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 统计相关 接口
 * Created by jie on 16/12/22.
 */

public interface IStatistics {

    /**
     * 第一次获取 【销售统计全部】数据
     */
    @GET("/statistics/query")
    Observable<HttpStatistics> getNoticeList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 【过程统计】 的 今日 本周 数据
     */
    @GET("/statistics/process/query")
    Observable<List<HttpProcess>> getProcessList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 【增量统计】 的 今日 本周 数据
     */
    @GET("/statistics/bulking/query")
    Observable<List<HttpBulking>> getBulkingList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 【业绩目标】 的 本周 本月 数据
     */
    @GET("/statistics/achieve/query")
    Observable<List<HttpAchieves>> getAchievesList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 【销售漏斗】 的 本周 本月 数据
     */
    @GET("/statistics/salechance/query")
    Observable<List<HttpSalechance>> getFunnelList(@QueryMap HashMap<String, Object> map);
}
