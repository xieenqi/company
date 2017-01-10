package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.HomePaymentModel;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StatisticRecord;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 仪表盘数据接口
 * Created by yyy on 16/12/28.
 */

public interface IDashBoard {

    /**
     * 客户跟进/线索跟进
     */
    @GET("/statistics/sale-activity/mobile/total")
    Observable<BaseResponse<FollowupStatistic>> getFupCusClue(@QueryMap HashMap<String, Object> map);

    /**
     * 增量/存量
     */
    @GET("/statistics/customer_stock_increment/mobile/total")
    Observable<BaseResponse<ArrayList<StockStatistic>>> getStock(@QueryMap HashMap<String, Object> map);

    /**
     * 数量/金额
     */
    @GET("/statistics/order/mobile/total")
    Observable<BaseResponse<MoneyStatistic>> getMoneyCount(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘 客户/线索跟进 列表数据接口
     */
    @GET("/statistics/sale-activity/mobile/data")
    Observable<BaseResponse<PaginationX<StatisticRecord>>>
    getDashBoardFollwoUpListData(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘客户 拜访 列表数据接口
     */
    @GET("/statistics/visit/mobile/detail")
    Observable<BaseResponse<PaginationX<StatisticRecord>>>
    getDashBoardVisitListData(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘客户／跟进  电话录音 列表数据接口
     */
    @GET("/statistics/call_activity/mobile/detail")
    Observable<BaseResponse<PaginationX<StatisticRecord>>>
    getDashBoardCallListData(@QueryMap HashMap<String, Object> map);
    /**
     *增量/存量 明细列表接
     */
    @GET("/statistics/customer_stock_increment/mobile/detail")
    Observable<BaseResponse<PaginationX<StatisticRecord>>>
    getDashBoardCommonListData(@QueryMap HashMap<String, Object> map);
    /**
     *订单数量和金额明细列表接
     */
    @GET("/statistics/order/mobile/detail")
    Observable<BaseResponse<PaginationX<StatisticRecord>>>
    getDashBoardSaleListData(@QueryMap HashMap<String, Object> map);

    /**
     *获取首页回款柱状图
     */
    @GET("/statistics/mobile/back_money/total")
    Observable<BaseResponse<HomePaymentModel>>
    getDashBoardHomePaymentData();
}
