package com.loyo.oa.v2.ui.activity.attendance.bean;

import com.loyo.oa.v2.ui.activity.other.bean.User;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :考勤信息
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class AttendanceRecord implements Serializable {
    /**内勤**/
    public static final int OUT_STATE_OFFICE_WORK=0;
    /**已确认的外勤**/
    public static final int OUT_STATE_CONFIRMED_FIELD_WORK=1;
    /**未确认的外勤**/
    public static final int OUT_STATE_FIELD_WORK=2;

    /**非加班**/
    public static final int OUT_STATE_OFFICE_OVERTIME=0;
    /**已确认的加班**/
    public static final int OUT_STATE_CONFIRMED_FIELD_OVERTIME=1;
    /**未确认的加班**/
    public static final int OUT_STATE_FIELD_OVERTIME=2;

    /**请假或出差**/
    public static final int STATE_MASTER = 0;
    /**正常打卡**/
    public static final int STATE_NORMAL=1;
    /**迟到**/
    public static final int STATE_BE_LATE=STATE_NORMAL+1;
    /**早退**/
    public static final int STATE_LEAVE_EARLY=STATE_NORMAL+2;

    public static final int STATE_OVERWORK = 5;


    private String address;// (string, optional): ,
    private String attachementuuid;// (string, optional): ,
    private int checkindate; //(int, optional): ,
    private long confirmtime;// (int64, optional): ,
    private User confirmuser;// (&{organization User}, optional): ,
    private long createtime;// (int64, optional): ,
    private String gpsinfo;// (string, optional): ,
    private String id;// (int64, optional): ,
    private int inorout;// (int, optional): ,
    private String originalgps;// (string, optional): ,
    private int outstate;// (int, optional): ,
    private String reason;// (string, optional): ,
    private int state;// (int, optional): ,
    private int tagstate;// (int, optional): ,
    private long updatetime;// (int64, optional):
    private int remainTime;
    private int extraTime;
    private int extraState;
    private long extraWorkStartTime;
    private int confirmExtraTime;
    private int leaveDays;

    public int getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        this.leaveDays = leaveDays;
    }

    public int getConfirmExtraTime() {
        return confirmExtraTime;
    }

    public void setConfirmExtraTime(int confirmExtraTime) {
        this.confirmExtraTime = confirmExtraTime;
    }

    public long getExtraWorkStartTime() {
        return extraWorkStartTime;
    }

    public void setExtraWorkStartTime(long extraWorkStartTime) {
        this.extraWorkStartTime = extraWorkStartTime;
    }

    public int getExtraState() {
        return extraState;
    }

    public void setExtraState(int extraState) {
        this.extraState = extraState;
    }

    public int getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(int extraTime) {
        this.extraTime = extraTime;
    }

    private User user;// (&{organization User}, optional):

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTagstate() {
        return tagstate;
    }

    public void setTagstate(int tagstate) {
        this.tagstate = tagstate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getOutstate() {
        return outstate;
    }

    public void setOutstate(int outstate) {
        this.outstate = outstate;
    }

    public String getOriginalgps() {
        return originalgps;
    }

    public void setOriginalgps(String originalgps) {
        this.originalgps = originalgps;
    }

    public int getInorout() {
        return inorout;
    }

    public void setInorout(int inorout) {
        this.inorout = inorout;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getGpsinfo() {
        return gpsinfo;
    }

    public void setGpsinfo(String gpsinfo) {
        this.gpsinfo = gpsinfo;
    }

    public User getConfirmuser() {
        return confirmuser;
    }

    public void setConfirmuser(User confirmuser) {
        this.confirmuser = confirmuser;
    }

    public long getConfirmtime() {
        return confirmtime;
    }

    public void setConfirmtime(long confirmtime) {
        this.confirmtime = confirmtime;
    }

    public String getAttachementuuid() {
        return attachementuuid;
    }

    public void setAttachementuuid(String attachementuuid) {
        this.attachementuuid = attachementuuid;
    }

    public int getCheckindate() {
        return checkindate;
    }

    public void setCheckindate(int checkindate) {
        this.checkindate = checkindate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
