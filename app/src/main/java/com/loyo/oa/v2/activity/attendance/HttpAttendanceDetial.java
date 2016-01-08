package com.loyo.oa.v2.activity.attendance;

import com.loyo.oa.v2.beans.User;

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

    public String attachementuuid;
    public String originalgps;
    public String gpsinfo;
    public String address;
    public String reason;
    public int state;
    public int outstate;
    public int tagstate;

    public long confirmtime;
    public User confirmuser;


}