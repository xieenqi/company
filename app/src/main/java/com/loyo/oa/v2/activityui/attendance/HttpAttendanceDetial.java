package com.loyo.oa.v2.activityui.attendance;

import com.loyo.oa.v2.activityui.other.model.User;

import java.io.Serializable;

/**
 * Created by pj on 16/1/8.
 */
public class HttpAttendanceDetial implements Serializable {

    public String id;
    public User user;

    public int inorout;
    public long createtime;
    public long updatetime;
    public long extraWorkStartTime;
    public long extraWorkEndTime;

    public String attachementuuid;
    public String originalgps;
    public String gpsinfo;
    public String address;
    public String reason;
    public int state;
    public int outstate;
    public int tagstate;
    public int extraState;

    public long confirmtime;
    public User confirmuser;

}
