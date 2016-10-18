package com.loyo.oa.upload.alioss;

/**
 * 【阿里云附件】token
 * Created by yyy on 16/8/8.
 */
public class OssToken {

    public String RequestId;
    public FederatedUser FederatedUser;
    public Credentials Credentials;

    public class Credentials {

        public String AccessKeySecret;
        public String AccessKeyId;
        public String Expiration;
        public String SecurityToken;

    }

    public class FederatedUser {
    }

}
