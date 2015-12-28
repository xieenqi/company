package com.loyo.oa.v2.jpush;

import java.io.Serializable;

/**
 * Created by pj on 15/12/28.
 */
public class HttpJpushNotification implements Serializable{
    public String buzzId;
    public int buzzType;
    public String operationType;
}
