package com.loyo.oa.v2.common.event;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/9/1.
 */
public class BaseEvent implements Serializable {
    public int eventCode; /* 主事件码*/
    public int subCode;   /* 子事件码 */
    public String request;
    public String session;

    public Bundle bundle; /* 数据 */

    public BaseEvent(){
        eventCode = 0;
        subCode = 1;
    }
}
