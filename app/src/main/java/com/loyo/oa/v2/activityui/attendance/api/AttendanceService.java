package com.loyo.oa.v2.activityui.attendance.api;

import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;
import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * 考勤打卡  网络请求
 * Created by jie on 16/12/20.
 */

public class AttendanceService {


    //验证是否能打卡
    public static Observable<ValidateInfo> validateAttendance() {
        return  RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                        .validateAttendance().compose(RetrofitAdapterFactory.<ValidateInfo>compatApplySchedulers());
    }

    // 刷新打卡位置
    public static Observable<Object> refreshLocation(String originalGPS) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                        .refreshLocation(originalGPS).compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    // 确认打卡
    public static Observable<AttendanceRecord> confirmAttendance(HashMap<String,Object> map) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                        .confirmAttendance(map).compose(RetrofitAdapterFactory.<AttendanceRecord>compatApplySchedulers());
    }

    // 用户打卡
    public static Observable<AttendanceRecord> checkAttendance(HashMap<String,Object> map) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                .checkAttendance(map).compose(RetrofitAdapterFactory.<AttendanceRecord>compatApplySchedulers());
    }

    // 获取考勤打卡列表
    public static Observable<HttpAttendanceList> getAttendances(HashMap<String,Object> map) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                .getAttendances(map).compose(RetrofitAdapterFactory.<HttpAttendanceList>compatApplySchedulers());
    }

    // 确认外勤
    public static Observable<AttendanceRecord> confirmOutAttendance(String  id, int type) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                .confirmOutAttendance(id,type).compose(RetrofitAdapterFactory.<AttendanceRecord>compatApplySchedulers());
    }

    // 获取考勤打卡详细内容
    public static Observable<HttpAttendanceDetial> getAttendancesDetial(String  id) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                .getAttendancesDetial(id).compose(RetrofitAdapterFactory.<HttpAttendanceDetial>compatApplySchedulers());
    }

    // 团队考勤列表，获取统计数据  加 checkindate  20160429
    public static Observable<AttendanceList> getTeamCount(int checkindate) {
        return RetrofitAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class)
                .getTeamCount(checkindate).compose(RetrofitAdapterFactory.<AttendanceList>compatApplySchedulers());
    }






}
