package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/9/9.
 */
public class CallClientInfo implements Serializable {

    public Resp resp;

    public class Resp {

        public String respCode;
        public String count;
        public Client client;

    }

    public class Client {

        public String balance;
        public String clientNumber;
        public String clientPwd;
        public String clientType;
        public String createDate;
        public String friendlyName;
        public String mobile;
        public String room;
    }

}
