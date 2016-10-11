package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import java.util.HashMap;

/**
 * Created by yyy on 16/10/11.
 */

public interface AttendanceListPresenterImpl {

    /*获取列表数据*/
    void getListData(int type,int qtime,int page);

    /*获取团队数据*/
    void getTeamData(int qtime);

    /*打卡验证信息获取*/
    void getValidateInfo();

    /*打卡信息检查*/
    void checkAttendance(HashMap<String,Object> map,String address);
}
