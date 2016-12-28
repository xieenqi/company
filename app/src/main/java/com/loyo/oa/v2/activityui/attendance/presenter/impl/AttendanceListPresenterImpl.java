package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.v2.activityui.attendance.api.AttendanceService;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceListPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceListView;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import java.util.HashMap;

/**
 * 【考勤列表】
 * Created by yyy on 16/10/11.
 */

public class AttendanceListPresenterImpl implements AttendanceListPresenter {

    public Activity mActivity;
    public AttendanceListView crolView;

    public AttendanceListPresenterImpl(Activity mActivity, AttendanceListView crolView){
        this.mActivity = mActivity;
        this.crolView  = crolView;

    }

    /**
     * 获取列表信息
     * */
    @Override
    public void getListData(final int type, int qtime, final int page) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        map.put("qtime", qtime);
        map.put("pageIndex", page);
        map.put("pageSize", 2000);
//        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).getAttendances(map, new RCallback<HttpAttendanceList>() {
//            @Override
//            public void success(HttpAttendanceList mAttendanceList, Response response) {
//                HttpErrorCheck.checkResponse(type + " 考勤列表的数据：", response);
//                crolView.getListDataEmbl(mAttendanceList);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error,crolView.getLoading());
//                super.failure(error);
//            }
//        });

        AttendanceService.getAttendances(map).subscribe(new DefaultLoyoSubscriber<HttpAttendanceList>(crolView.getLoading()) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }
            @Override
            public void onNext(HttpAttendanceList mAttendanceList) {
                DialogHelp.cancelLoading();
                crolView.getListDataEmbl(mAttendanceList);
            }
        });
    }

    /**
     * 获取团队统计信息(迟到,早退...)
     * */
    @Override
    public void getTeamData(int qtime) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class).
//                getTeamCount(getDateTime((long) qtime), new RCallback<AttendanceList>() {
//                    @Override
//                    public void success(AttendanceList mAttendanceList, Response response) {
//                        HttpErrorCheck.checkResponse(" 团队Count：", response);
//                        crolView.getTeamDataEmbl(mAttendanceList);
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error);
//                        super.failure(error);
//                    }
//                });

        AttendanceService.getTeamCount(qtime).subscribe(new DefaultLoyoSubscriber<AttendanceList>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }
            @Override
            public void onNext(AttendanceList mAttendanceList) {
                DialogHelp.cancelLoading();

                crolView.getTeamDataEmbl(mAttendanceList);

            }
        });
    }



    /**
     * 打卡信息验证
     * */
    @Override
    public void getValidateInfo() {
//        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
//            @Override
//            public void success(final ValidateInfo mValidateInfo, final Response response) {
//                HttpErrorCheck.checkResponse("考勤信息1:", response);
//                crolView.getValidateInfoEmbl(mValidateInfo);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//            }
//        });
        AttendanceService.validateAttendance().subscribe(new DefaultLoyoSubscriber<ValidateInfo>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }

            @Override
            public void onNext(ValidateInfo validateInfo) {
                DialogHelp.cancelLoading();
                crolView.getValidateInfoEmbl(validateInfo);
            }
        });
    }

    /**
     * 打卡信息检查
     * */
    @Override
    public void checkAttendance(HashMap<String,Object> map, final String address) {
//        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
//            @Override
//            public void success(final AttendanceRecord mRecord, final Response response) {
//                HttpErrorCheck.checkResponse("考勤信息2：", response);
//                crolView.checkAttendanceEmbl(mRecord,address);
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//            }
//        });

        AttendanceService.checkAttendance(map).subscribe(new DefaultLoyoSubscriber<AttendanceRecord>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                DialogHelp.cancelLoading();
            }
            @Override
            public void onNext(AttendanceRecord mRecord) {
                DialogHelp.cancelLoading();
                crolView.checkAttendanceEmbl(mRecord,address);
            }
        });
    }

    private int getDateTime(long qtime) {
//        return Integer.valueOf(MainApp.getMainApp().df4.format(new Date((qtime * 1000))).replace(".", ""));
        return Integer.valueOf(DateFormatSet.dateNumSdf.format(qtime*1000));
    }
}
