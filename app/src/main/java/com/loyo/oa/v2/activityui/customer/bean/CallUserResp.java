package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/9/9.
 */
public class CallUserResp implements Serializable{

    public Resp resp = new Resp();

    public class Resp{
        public String respCode;
        public Client client;
    }

    public class Client{
        public String balance;
        public String clientNumber;
        public String clientPwd;
        public String createDate;
    }

}
