package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;
import com.loyo.oa.v2.activityui.dashboard.model.DashBoardListModel;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyCountModel;
import com.loyo.oa.v2.activityui.dashboard.model.StockListModel;

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
    Observable<CsclueFowUp> getFupCusClue(@QueryMap HashMap<String, Object> map);

    /**
     * 增量/存量
     */
    @GET("/statistics/customer_stock_increment/mobile/total")
    Observable<StockListModel> getStock(@QueryMap HashMap<String, Object> map);

    /**
     * 数量/金额
     */
    @GET("/statistics/order/mobile/total")
    Observable<MoneyCountModel> getMoneyCount(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘 客户/线索跟进 列表数据接口
     */
    @GET("/statistics/sale-activity/mobile/data")
    Observable<DashBoardListModel> getDashBoardFollwoUpListData(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘客户 拜访 列表数据接口
     */
    @GET("/statistics/visit/mobile/detail")
    Observable<DashBoardListModel> getDashBoardVisitListData(@QueryMap HashMap<String, Object> map);

    /**
     *仪表盘客户／跟进  电话录音 列表数据接口
     */
    @GET("/statistics/call_activity/mobile/detail")
    Observable<DashBoardListModel> getDashBoardCallListData(@QueryMap HashMap<String, Object> map);
    /**
     *增量/存量 明细列表接
     */
    @GET("/statistics/customer_stock_increment/mobile/detail")
    Observable<DashBoardListModel> getDashBoardCommonListData(@QueryMap HashMap<String, Object> map);
}
