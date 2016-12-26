package com.loyo.oa.v2.common;

/**
 * 错误提醒方式
 * Created by xeq on 16/12/20.
 */

public enum RemindWay {

    TOAST(0), DILOG(1);
    public int type;

    RemindWay(int type) {
        this.type = type;
    }
}
