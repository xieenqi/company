package com.loyo.oa.v2.tool;

import com.loyo.oa.v2.BuildConfig;

public class Config_project {

    //    public static final Boolean is_developer_mode = false; //生产模式
    public static final Boolean is_developer_mode = BuildConfig.DEBUG;

    public static String API_URL() {
        return SERVER_URL().concat("/api/v2/oa");
    }

//    public static String API_URL() {
//        return API_URL_CUSTOMER();
//    }
    public static String API_URL_CUSTOMER() {
        return SERVER_URL_CUSTOMER().concat("/api/v2");
    }



    public static String API_URL_ATTACHMENT() {
        return SERVER_URL_ATTACHMENT().concat("/api/v2");
    }

    /**
     * 附件地址
     * @return
     */
    public static String SERVER_URL_ATTACHMENT() {
        return "http://192.168.31.131:8030";
    }



    public static String API_URL_EXTRA() {
        return SERVER_URL_EXTRA().concat("/api/v2/");
    }

    /**
     * 讨论地址
     * @return
     */
    public static String SERVER_URL_EXTRA() {
        return "http://192.168.31.131:8050";
    }

    /**
     * 客户地址
     * @return
     */
    public static String SERVER_URL_CUSTOMER() {
        return "http://192.168.31.131:8090";
    }

    /**
     * 登录地址
     * @return
     */
    public static String SERVER_URL_LOGIN() {
        return "http://192.168.31.131:88";
    }


    public static String SERVER_URL() {
        if (is_developer_mode) {

            return "http://192.168.31.131:8070";
        }
        return "http://app.361loyo.com:8090";
        //        return "http://app.361loyo.com";
    }
}
