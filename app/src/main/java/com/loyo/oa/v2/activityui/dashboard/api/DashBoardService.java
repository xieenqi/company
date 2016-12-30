package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;
import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StatisticRecord;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/28.
 */

public class DashBoardService {

    //获取客户和线索跟进
    public static Observable<FollowupStatistic> getFupCusClue(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getFupCusClue(params)
                        .compose(RetrofitAdapterFactory.<FollowupStatistic>applySchedulers());
    }


    //获取增量和存量
    public static Observable<ArrayList<StockStatistic>> getStock(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getStock(params)
                        .compose(RetrofitAdapterFactory.<ArrayList<StockStatistic>>applySchedulers());
    }

    //获取数量和金额
    public static Observable<MoneyStatistic> getMoneyCount(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getMoneyCount(params)
                        .compose(RetrofitAdapterFactory.<MoneyStatistic>applySchedulers());
    }

    //仪表盘 列表的数据接口
    public static Observable<PaginationX<StatisticRecord>>
    getDashBoardListData(HashMap<String, Object> params, DashboardType type) {
        IDashBoard dashBoard = RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                .create(IDashBoard.class);
        Observable<BaseResponse<PaginationX<StatisticRecord>>> observable = null;
        switch (type){
            case CUS_FOLLOWUP:
            case SALE_FOLLOWUP:
                //跟进
                observable = dashBoard.getDashBoardFollwoUpListData(params);
                break;
            case CUS_SIGNIN:
                //客户拜访
                observable = dashBoard.getDashBoardVisitListData(params);
                break;
            case COMMON:
                //增量，存量
                observable = dashBoard.getDashBoardCommonListData(params);
                break;
            case CUS_CELL_RECORD:
                //客户电话录音
            case SALE_CELL_RECORD:
                //线索电话录音
                observable = dashBoard.getDashBoardCallListData(params);
                break;
            case ORDER_MONEY:
                //订单金额
            case ORDER_NUMBER:
                //订单数量
                observable = dashBoard.getDashBoardSaleListData(params);
                break;
        }
       return observable.compose(RetrofitAdapterFactory.<PaginationX<StatisticRecord>>applySchedulers());

    }


}
