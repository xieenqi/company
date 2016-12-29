package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.common.DashborardType;
import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;
import com.loyo.oa.v2.activityui.dashboard.model.DashBoardListModel;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyCountModel;
import com.loyo.oa.v2.activityui.dashboard.model.StockListModel;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import java.util.HashMap;
import rx.Observable;

/**
 * Created by yyy on 16/12/28.
 */

public class DashBoardService {

    //获取客户和线索跟进
    public static Observable<CsclueFowUp> getFupCusClue(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getFupCusClue(params)
                        .compose(RetrofitAdapterFactory.<CsclueFowUp>compatApplySchedulers());
    }


    //获取增量和存量
    public static Observable<StockListModel> getStock(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getStock(params)
                        .compose(RetrofitAdapterFactory.<StockListModel>compatApplySchedulers());
    }

    //获取数量和金额
    public static Observable<MoneyCountModel> getMoneyCount(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getMoneyCount(params)
                        .compose(RetrofitAdapterFactory.<MoneyCountModel>compatApplySchedulers());
    }

    //仪表盘 列表的数据接口
    public static Observable<DashBoardListModel> getDashBoardListData(HashMap<String, Object> params,DashborardType type) {
        IDashBoard dashBoard = RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                .create(IDashBoard.class);
        Observable<DashBoardListModel> observable = null;
        switch (type){
            case CUS_FOLLOWUP:
            case SALE_FOLLOWUP:
                observable = dashBoard.getDashBoardFollwoUpListData(params);
                break;
            case CUS_SIGNIN:
                observable = dashBoard.getDashBoardVisitListData(params);
                break;
            case COMMON:
                observable = dashBoard.getDashBoardCommonListData(params);
                break;
            case CUS_CELL_RECORD:
            case SALE_CELL_RECORD:
                observable = dashBoard.getDashBoardCallListData(params);
                break;
        }
       return observable.compose(RetrofitAdapterFactory.<DashBoardListModel>compatApplySchedulers());

    }


}
