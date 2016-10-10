package com.loyo.oa.v2.activityui.attendance.model;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :考勤打卡时间
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class TimeSetting implements Serializable {

    private long checkInTime;// (int64, optional):
    private long checkOutTime;// (int64, optional):

    public long getCheckOutTime() {
        return checkOutTime*1000;
    }

    public void setCheckOutTime(long checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public long getCheckInTime() {
        return checkInTime*1000;
    }

    public void setCheckInTime(long checkInTime) {
        this.checkInTime = checkInTime;
    }
}
