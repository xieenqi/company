package com.loyo.oa.v2.activityui.attendance;

import com.loyo.oa.v2.activityui.attendance.bean.AttendanceList;

import java.io.Serializable;

/**考勤列表返回 jso模板
 * Created   xnq 15/12/8.
 */
public class HttpAttendanceList implements Serializable{
    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public AttendanceList records;
}
