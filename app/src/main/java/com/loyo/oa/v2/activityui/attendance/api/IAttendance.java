package com.loyo.oa.v2.activityui.attendance.api;

import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 考勤打卡的 服务器接口
 * Created by jie on 16/12/20.
 */

public interface IAttendance {
    /**
     * 验证是否能打卡
     */
    @GET("/attendance/valid")
    Observable<ValidateInfo> validateAttendance();

    /**
     * 刷新打卡位置
     */
    @GET("/attendance/refresh")
    Observable<Object> refreshLocation(@Query("originalgps") String originalGPS);

    /**
     * 确认打卡
     */
    @POST("/attendance/save")
    Observable<AttendanceRecord> confirmAttendance(@Body HashMap<String,Object> map);

    /**
     * 用户打卡
     */
    @POST("/attendance/check")
    Observable<AttendanceRecord> checkAttendance(@Body HashMap<String,Object> map);

    /**
     * 获取考勤打卡列表
     */
    @GET("/attendance/list")
    Observable<HttpAttendanceList> getAttendances(@QueryMap HashMap<String,Object> map);

    /**
     * 确认外勤
     */
    @PUT("/attendance/{id}")
    Observable<AttendanceRecord> confirmOutAttendance(@Path("id") String  id, @Query("type") int type);


    /**
     * 获取考勤打卡详细内容
     */
    @GET("/attendance/{id}")
    Observable<HttpAttendanceDetial> getAttendancesDetial(@Path("id") String id);

    /**
     * 团队考勤列表，获取统计数据  加 checkindate  20160429
     * */
    @GET("/attendance/team/count")
    Observable<AttendanceList> getTeamCount(@Query("checkindate") int checkindate);


}
