package com.loyo.oa.v2.activityui.attendance.presenter.impl;

/**
 * Created by loyo_dev1 on 16/10/11.
 */

public interface AttendanceDetailsPresenterImpl {

    /*获取附件*/
    void getAttachments(String attachementuuid);

    /*获取考勤详情*/
    void getData(String attendanceId);

    /*确认外勤(加班)*/
    void confirmOutAttendance(String attendanceId,int type);
}
