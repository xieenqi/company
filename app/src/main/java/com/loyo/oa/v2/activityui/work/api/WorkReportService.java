package com.loyo.oa.v2.activityui.work.api;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.work.bean.HttpDefaultComment;
import com.loyo.oa.v2.activityui.work.bean.WorkReportDyn;
import com.loyo.oa.v2.activityui.work.bean.WorkReportTpl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * 工作报告 网络服务
 * Created by jie on 16/12/21.
 */

public class WorkReportService {
    private static RestAdapter restAdapter;

    //获取动态工作报告
    public static Observable<ArrayList<WorkReportDyn>> getDynamic(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.SIGNLN_TEM)
                        .create(IWorkReport.class)
                        .getDynamic(params)
                        .compose(RetrofitAdapterFactory.<ArrayList<WorkReportDyn>>compatApplySchedulers());
    }

    //获取一个RestAdapter
    private static RestAdapter getRestAdapter() {
        if (restAdapter == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));
                    request.addHeader("LoyoPlatform", Utils.getCellInfo().getLoyoPlatform());
                    request.addHeader("LoyoAgent", Utils.getCellInfo().getLoyoAgent());
                    request.addHeader("LoyoOSVersion", Utils.getCellInfo().getLoyoOSVersion());
                    request.addHeader("LoyoVersionName", Global.getVersionName());
                    request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));
                }
            };

            restAdapter = new RestAdapter.Builder().setEndpoint(Config_project.API_URL()).
                    setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(requestInterceptor).build();
        }
        return restAdapter;
    }

    //根据ID获取报告详情
    public static Observable<WorkReport> getWorkReportDetail(String id, String key) {
        return getRestAdapter().create(IWorkReport.class).getWorkReportDetail(id,key)
                        .compose(RetrofitAdapterFactory.<WorkReport>compatApplySchedulers());
    }
//     没有地方使用
//    //根据筛选条件获取报告列表
//    public static Observable<PaginationX<WorkReport>> getWorkReports(HashMap<String, Object> params) {
//        return
//                RetrofitAdapterFactory.getInstance()
//                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
//                        .create(IWorkReport.class)
//                        .getWorkReports(params)
//                        .compose(RetrofitAdapterFactory.<PaginationX<WorkReport>>compatApplySchedulers());
//    }

    //根据筛选条件获取报告列表(v2.2 精简接口)
    public static Observable<PaginationX<WorkReportRecord>> getWorkReportsData(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWorkReport.class)
                        .getWorkReportsData(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<WorkReportRecord>>compatApplySchedulers());
    }

    //点评报告
    public static Observable<WorkReport> reviewWorkReport( String id,HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWorkReport.class)
                        .reviewWorkReport(id,params)
                        .compose(RetrofitAdapterFactory.<WorkReport>compatApplySchedulers());
    }

    //创建报告
    public static Observable<WorkReport> createWorkReport(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWorkReport.class)
                        .createWorkReport(params)
                        .compose(RetrofitAdapterFactory.<WorkReport>compatApplySchedulers());
    }

    //删除报告
    public static Observable<WorkReport> deleteWorkReport(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWorkReport.class)
                        .deleteWorkReport(id)
                        .compose(RetrofitAdapterFactory.<WorkReport>compatApplySchedulers());
    }


    //更新报告
    public static Observable<WorkReport> updateWorkReport(String id,HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWorkReport.class)
                        .updateWorkReport(id,map)
                        .compose(RetrofitAdapterFactory.<WorkReport>compatApplySchedulers());
    }

    //获取默认的点评人
    public static Observable<HttpDefaultComment> defaultComment() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.ADD_WORK_REPORT_PL)
                        .create(IWorkReport.class)
                        .defaultComment()
                        .compose(RetrofitAdapterFactory.<HttpDefaultComment>compatApplySchedulers());
    }
}
