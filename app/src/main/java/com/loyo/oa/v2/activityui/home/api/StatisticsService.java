package com.loyo.oa.v2.activityui.home.api;

import com.loyo.oa.v2.activityui.home.bean.HttpAchieves;
import com.loyo.oa.v2.activityui.home.bean.HttpBulking;
import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.activityui.home.bean.HttpProcess;
import com.loyo.oa.v2.activityui.home.bean.HttpSalechance;
import com.loyo.oa.v2.activityui.home.bean.HttpStatistics;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * 统计相关 服务
 * Created by jie on 16/12/22.
 */

public class StatisticsService {

    //第一次获取 【销售统计全部】数据
    public static Observable<HttpStatistics> getNoticeList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IStatistics.class)
                        .getNoticeList(map)
                        .compose(RetrofitAdapterFactory.<HttpStatistics>compatApplySchedulers());
    }

    //获取 【过程统计】 的 今日 本周 数据
    public static Observable<List<HttpProcess>> getProcessList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IStatistics.class)
                        .getProcessList(map)
                        .compose(RetrofitAdapterFactory.<List<HttpProcess>>compatApplySchedulers());
    }


    //获取 【增量统计】 的 今日 本周 数据
    public static Observable<List<HttpBulking>> getBulkingList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IStatistics.class)
                        .getBulkingList(map)
                        .compose(RetrofitAdapterFactory.<List<HttpBulking>>compatApplySchedulers());
    }


    //获取 【业绩目标】 的 本周 本月 数据
    public static Observable<List<HttpAchieves>> getAchievesList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IStatistics.class)
                        .getAchievesList(map)
                        .compose(RetrofitAdapterFactory.<List<HttpAchieves>>compatApplySchedulers());
    }

    //获取 【销售漏斗】 的 本周 本月 数据
    public static Observable<List<HttpSalechance>> getFunnelList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IStatistics.class)
                        .getFunnelList(map)
                        .compose(RetrofitAdapterFactory.<List<HttpSalechance>>compatApplySchedulers());
    }
}
