package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;

/**
 * Created by yyy on 16/9/9.
 */
public class CallBackCallid implements Serializable{


    public int errcode;
    public String errmsg;
    public Data data;

    public class Data{
       public String callLogId;
    }
}
