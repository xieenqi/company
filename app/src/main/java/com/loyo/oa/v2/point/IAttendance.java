package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.attendance.HttpAttendanceDetial;
import com.loyo.oa.v2.activity.attendance.HttpAttendanceList;
import com.loyo.oa.v2.beans.AttendanceList;
import com.loyo.oa.v2.beans.AttendancePhoto;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.ValidateInfo;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 描述 :考勤打卡与服务器接口
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public interface IAttendance {


    @GET("/attendance")
    void getAttendancePhoto(Callback<ArrayList<AttendancePhoto>> callback);

    /**
     * 验证是否能打卡
     * @param callback
     */
    @GET("/attendance/valid")
    void validateAttendance(Callback<ValidateInfo> callback);

    /**
     * 刷新打卡位置
     * @param originalgps
     * @param callback
     */
    @GET("/attendance/refresh")
    void refreshLocation(@Query("originalgps") String originalgps,Callback<Object> callback);

    /**
     * 确认打卡
     * @param map
     * @param callback
     */
    @POST("/attendance/save")
    void confirmAttendance(@Body HashMap<String,Object> map, Callback<AttendanceRecord> callback);

    /**
     * 用户打卡
     * @param map
     * @param callback
     */
    @POST("/attendance/check")
    void checkAttendance(@Body HashMap<String,Object> map, Callback<AttendanceRecord> callback);

    /**
     * 获取考勤打卡列表
     * @param map
     * @param callback
     */
    @GET("/attendance/list")
    void getAttendances(@QueryMap HashMap<String,Object> map, Callback<HttpAttendanceList> callback);

    /**
     * 确认外勤
     * @param id
     * @param callback
     */
    @PUT("/attendance/{id}")
    void confirmOutAttendance(@Path("id") String  id,Callback<AttendanceRecord> callback);


    /**
     * 获取考勤打卡详细内容
     * @param callback
     */
    @GET("/attendance/{id}")
    void getAttendancesDetial(@Path("id") String id, Callback<HttpAttendanceDetial> callback);

    /**
     * 团队考勤列表，获取统计数据
     * */
    @GET("/attendance/team/count")
    void getTeamCount(Callback<AttendanceList> callback);


}
