package com.loyo.oa.v2.activityui.discuss.api;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussDet;
import com.loyo.oa.v2.activityui.discuss.HttpDiscussItem;
import com.loyo.oa.v2.activityui.discuss.HttpMyDiscussItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * 讨论模块服务
 * Created by jie on 16/12/21.
 */

public class DiscussService {
    //分页获取【我的讨论】列表
    public static Observable<PaginationX<HttpDiscussItem>> getDiscussList(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_EXTRA())
                        .create(IDiscuss.class)
                        .getDisscussList(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<HttpDiscussItem>>compatApplySchedulers());
    }

    //分页获取【@我讨论】列表
    public static Observable<PaginationX<HttpMyDiscussItem>> getMyDisscussList(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_EXTRA())
                        .create(IDiscuss.class)
                        .getMyDisscussList(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<HttpMyDiscussItem>>compatApplySchedulers());
    }

    //获取讨论详细
    public static Observable<PaginationX<HttpDiscussDet>> getDiscussDetail(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDiscuss.class)
                        .getDiscussDetail(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<HttpDiscussDet>>compatApplySchedulers());
    }

    //获取讨论详细
    public static Observable<Object> updateReadDot(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDiscuss.class)
                        .updateReadDot(params)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
}
