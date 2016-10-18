package com.loyo.oa.v2.activityui.attendance.viewcontrol;

import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.DayofAttendance;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;

/**
 * Created by yyy on 16/10/11.
 */

public interface AttendanceListView {

    /*获取列表数据成功处理*/
    void getListDataEmbl(HttpAttendanceList result);

    /*获取团队数据成功处理*/
    void getTeamDataEmbl(AttendanceList mAttendanceList);

    /*弹出提示*/
    void messageToat(String message);

    /*获取打卡信息验证成功处理*/
    void getValidateInfoEmbl(ValidateInfo mValidateInfo);

    /*查看考勤信息*/
    void previewAttendance(int inOrOut, DayofAttendance attendance, String overTime);

    /*检查考勤信息处理*/
    void checkAttendanceEmbl(AttendanceRecord attendanceRecord,String address);

}
