package com.loyo.oa.v2.common.Event;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/9/1.
 */
public class BaseEvent implements Serializable {
    public int eventCode;
    public Bundle bundle;
    public BaseEvent(){
        eventCode = 0;
    }
}
