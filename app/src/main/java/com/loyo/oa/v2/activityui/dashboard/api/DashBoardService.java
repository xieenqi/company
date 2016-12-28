package com.loyo.oa.v2.activityui.dashboard.api;

import com.loyo.oa.v2.activityui.dashboard.model.CsclueFowUp;
import com.loyo.oa.v2.activityui.dashboard.model.DashBoardListModel;
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


    //仪表盘 客户/线索跟进 列表数据接口
    public static Observable<DashBoardListModel> getDashBoardListData(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getDashBoardFollwoUpListData(params)
                        .compose(RetrofitAdapterFactory.<DashBoardListModel>compatApplySchedulers());
    }
    //仪表盘客户 拜访 列表数据接口
    public static Observable<DashBoardListModel> getDashBoardVisitListData(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getDashBoardVisitListData(params)
                        .compose(RetrofitAdapterFactory.<DashBoardListModel>compatApplySchedulers());
    }
    //仪表盘客户／跟进  电话录音 列表数据接口
    public static Observable<DashBoardListModel> getDashBoardCallListData(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IDashBoard.class)
                        .getDashBoardCallListData(params)
                        .compose(RetrofitAdapterFactory.<DashBoardListModel>compatApplySchedulers());
    }

}
