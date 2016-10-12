package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;

import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceListPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.Date;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).getAttendances(map, new RCallback<HttpAttendanceList>() {
            @Override
            public void success(HttpAttendanceList mAttendanceList, Response response) {
                HttpErrorCheck.checkResponse(type + " 考勤列表的数据：", response);
                crolView.getListDataEmbl(mAttendanceList);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkErrorForAttendance(error);
                super.failure(error);
            }
        });
    }

    /**
     * 获取团队信息
     * */
    @Override
    public void getTeamData(int qtime) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class).
                getTeamCount(getDateTime((long) qtime), new RCallback<AttendanceList>() {
                    @Override
                    public void success(AttendanceList mAttendanceList, Response response) {
                        HttpErrorCheck.checkResponse(" 团队Count：", response);
                        crolView.getTeamDataEmbl(mAttendanceList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });
    }



    /**
     * 打卡信息验证
     * */
    @Override
    public void getValidateInfo() {
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(final ValidateInfo mValidateInfo, final Response response) {
                HttpErrorCheck.checkResponse("考勤信息1:", response);
                crolView.getValidateInfoEmbl(mValidateInfo);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 打卡信息检查
     * */
    @Override
    public void checkAttendance(HashMap<String,Object> map, final String address) {
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(final AttendanceRecord mRecord, final Response response) {
                HttpErrorCheck.checkResponse("考勤信息2：", response);
                crolView.checkAttendanceEmbl(mRecord,address);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    private int getDateTime(long qtime) {
        return Integer.valueOf(MainApp.getMainApp().df4.format(new Date((qtime * 1000))).replace(".", ""));
    }
}
