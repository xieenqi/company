package com.loyo.oa.v2.beans;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/9/2.
 */
public class ServerTime {

    public long getNow() {
        return now*1000;
    }

    public void setNow(long now) {
        this.now = now;
    }

    private long now;
}
