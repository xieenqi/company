package com.loyo.oa.v2.ui.activity.attendance;

import com.loyo.oa.v2.ui.activity.attendance.bean.AttendanceList;

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
