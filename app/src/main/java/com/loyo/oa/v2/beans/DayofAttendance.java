package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :一天内的打卡列表
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class DayofAttendance implements Serializable {

    private String dateName;// (string, optional): ,
    private AttendanceRecord in;// (AttendanceRecord, optional): ,
    private AttendanceRecord out;// (AttendanceRecord, optional): ,
    private User user;// (&{organization User}, optional):

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AttendanceRecord getOut() {
        return out;
    }

    public void setOut(AttendanceRecord out) {
        this.out = out;
    }

    public AttendanceRecord getIn() {
        return in;
    }

    public void setIn(AttendanceRecord in) {
        this.in = in;
    }

    public String getDatename() {
        return dateName;
    }

    public void setDatename(String datename) {
        this.dateName = datename;
    }

}
