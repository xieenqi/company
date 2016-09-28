package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/9/9.
 */
public class CallBackResp implements Serializable{

    public Resp resp;

    public class Resp{
        public String respCode;
        public CallBack callback;
    }

    public class CallBack{
        public String callId;
        public String createDate;
    }

}
