package com.loyo.oa.v2.tool;

import com.loyo.oa.v2.BuildConfig;

/**
 * http 配置文件
 */
public class Config_project {

    public static boolean isRelease = false;                               //是否是正式产品

    public static final Boolean is_developer_mode = BuildConfig.DEBUG;     //dbug模式
    
//    public static String IP = "http://192.168.31.131";                     //内部测试环境

     public static String IP = "http://staging.ukuaiqi.com";                //产品预上线环境

    //public static String IP = "http://112.74.66.99";                     //产品预上线环境


    //public static String IP = "http://ukuaiqi.com";                      //网站产品正式环境

    //public static String IP = "http://192.168.31.136";

    //public static String IP = "http://192.168.31.139";

    public static String endpoint = "http://oss-cn-qingdao.aliyuncs.com";   //阿里云Oss

    protected Config_project() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    /**
     * 正式产品的域名
     */
    private static class Domain {

        /**
         * 账号中心
         */
        public static String account = "http://user.ukuaiqi.com";

        /**
         * crm 客户管理crm.ukuaiqi.co
         */
        public static String crm = "http://ukuaiqi.com/p/oa";

        /**
         * oa 系统oa.ukuaiqi.com
         */
        public static String oa = "http://ukuaiqi.com/p/oa";

        /**
         * attachment 附件
         */
        public static String attachment = "http://attachment.ukuaiqi.com";

        /**
         * discuss 讨论discuss.ukuaiqi.com
         */
        public static String discuss = "http://ukuaiqi.com/p/oa";

        /**
         * statistics 统计
         */
        public static String statistics = "http://stat.ukuaiqi.com";
    }

    /**
     * Oss bucketName
     */
    public static String OSS_UPLOAD_BUCKETNAME() {
        return isRelease ? "loyocloud-01" : "dev-loyocloud-01";
    }

    /**
     * 附件地址
     */
    public static String SERVER_URL_ATTACHMENT() {
        return isRelease ? Domain.attachment : IP + ":8030";
    }

    /**
     * 讨论地址
     */
    public static String SERVER_URL_EXTRA() {
        return isRelease ? Domain.discuss : IP + ":8070";
    }

    /**
     * 客户地址
     */
    public static String SERVER_URL_CUSTOMER() {
        return isRelease ? Domain.crm : IP + ":8070";
    }

    /**
     * 登录地址
     */
    public static String SERVER_URL_LOGIN() {
        return isRelease ? Domain.account : IP + ":8080";
    }

    /**
     * oa 系统
     */
    public static String SERVER_URL() {
        return isRelease ? Domain.oa : IP + ":8070";
    }

    /**
     * 统计
     */
    public static String SERVER_URL_STATISTICS() {
        return isRelease ? Domain.statistics : IP + ":9000";
    }

    public static String SIGNLN_TEM = SERVER_URL_STATISTICS() + "/api/v2";//团队拜访 列表

    public static String ADD_WORK_REPORT_PL = SERVER_URL() + "/api/v2/oa";//添加工作报告 的默认点评人

    public static String MAIN_RED_DOT = SERVER_URL_LOGIN() + "/api/v2/";//首页红点接口

    public static String GET_VERIFICATION_CODE = SERVER_URL_LOGIN() + "/oapi/sms";//绑定手机获取验证码

    public static String BIND_MOBLIE = SERVER_URL_LOGIN() + "/api/v2";//绑定手机完成

    public static String API_URL() {
        return SERVER_URL().concat("/api/v2/oa");
    }

    /**
     * 新版 上传轨迹
     */
    public static String NEW_UPLOCATION() {
        return isRelease ? "http://ukuaiqi.com" : IP;
    }

    public static String API_URL_CUSTOMER() {
        return SERVER_URL_CUSTOMER().concat("/api/v2");
    }

    public static String API_URL_ATTACHMENT() {
        return SERVER_URL_ATTACHMENT().concat("/api/v2");
    }

    public static String API_URL_EXTRA() {
        return SERVER_URL_EXTRA().concat("/api/v2/");
    }

    /**
     * 获取 销售统计 数据
     *
     * @return
     */
    public static String API_URL_STATISTICS() {
        return SERVER_URL().concat("/api/v2/");
    }

}