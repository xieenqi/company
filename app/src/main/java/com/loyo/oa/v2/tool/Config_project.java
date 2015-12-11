package com.loyo.oa.v2.tool;

/**
 * http 配置文件
 */
public class Config_project {

    public static final Boolean is_developer_mode = false; //生产模式
    public static String IP="http://114.215.83.227";

    //public static final Boolean is_developer_mode = BuildConfig.DEBUG;
    //public static String IP = "http://192.168.31.131";//内部服务器


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
     *
     * @return
     */
    public static String SERVER_URL_ATTACHMENT() {
        return IP + ":8030";
    }


    public static String API_URL_EXTRA() {
        return SERVER_URL_EXTRA().concat("/api/v2/");
    }

    /**
     * 讨论地址
     *
     * @return
     */
    public static String SERVER_URL_EXTRA() {
        return IP + ":8050";
    }

    /**
     * 客户地址
     *
     * @return
     */
    public static String SERVER_URL_CUSTOMER() {
        return IP + ":8090";
    }

    /**
     * 登录地址
     *
     * @return
     */
    public static String SERVER_URL_LOGIN() {
        return IP + ":88";
    }


    public static String SERVER_URL() {
        if (is_developer_mode) {

            return IP + ":8070";
        }
        return "http://app.361loyo.com";
        //        return "http://app.361loyo.com";
    }

    public static String URL() {

        return "http://app.361loyo.com";
        //        return "http://app.361loyo.com";
    }
}
