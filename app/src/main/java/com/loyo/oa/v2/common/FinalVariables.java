package com.loyo.oa.v2.common;

import com.loyo.oa.v2.tool.Config_project;


public final class FinalVariables {

    public static final int APP_RELOGIN = -4;

    public static final int REQUEST_DEAL_ATTACHMENT = 300;
    /**
     * 新增考勤
     */
    public static final int REQUEST_CHECKIN_ATTENDANCE = REQUEST_DEAL_ATTACHMENT + 1;
    /**
     * 预览外勤
     */
    public static final int REQUEST_PREVIEW_OUT_ATTENDANCE = REQUEST_DEAL_ATTACHMENT + 2;
    /**
     * 新增拜访
     */
    public static final int REQUEST_CREATE_LEGWORK = REQUEST_DEAL_ATTACHMENT + 3;
    /**
     * 查看拜访列表
     */
    public static final int REQUEST_PREVIEW_LEGWORKS = REQUEST_DEAL_ATTACHMENT + 4;
    /**
     * 查看拜访
     */
    public static final int REQUEST_PREVIEW_LEGWORK = REQUEST_DEAL_ATTACHMENT + 5;

    /**
     * 查看购买意向列表
     */
    public static final int REQUEST_PREVIEW_DEMANDS = REQUEST_DEAL_ATTACHMENT + 6;
    /**
     * 查看客户信息
     */
    public static final int REQUEST_PREVIEW_CUSTOMER_INFO = REQUEST_DEAL_ATTACHMENT + 7;
    /**
     * 查看客户联系人
     */
    public static final int REQUEST_PREVIEW_CUSTOMER_CONTACTS = REQUEST_DEAL_ATTACHMENT + 8;

    /**
     * 查看跟进列表
     */
    public static final int REQUEST_PREVIEW_CUSTOMER_ACTIVITIS = REQUEST_DEAL_ATTACHMENT + 9;

    /**
     * 查看任务列表
     */
    public static final int REQUEST_PREVIEW_CUSTOMER_TASKS = REQUEST_DEAL_ATTACHMENT + 10;

    /**
     * 新增任务
     */
    public static final int REQUEST_CREATE_TASK = REQUEST_DEAL_ATTACHMENT + 11;

    /**
     * 选择项目
     */
    public static final int REQUEST_SELECT_PROJECT = REQUEST_DEAL_ATTACHMENT + 12;

    /**
     * 关联客户
     */
    public static final int REQUEST_SELECT_CUSTOMER = REQUEST_DEAL_ATTACHMENT + 13;

    /*接口地址*/
    public static String GetLogin() {
        return Config_project.SERVER_URL() + "/oauth2/authorize";
    }

    /**
     * 企业QQ url
     */

/*    public static String GetBQQLogin() {
        return Config_project.URL() + "/oauth2/bqq/authorize" ;
    }*/

    /*    public static String GetLogin_success_prefix() {
        return Config_project.SERVER_URL() + "/oauth2/authorized?authorizationToken=" ;
    }*/
    public static String GetBQQLogin() {
        return "http://www.ukuaiqi.com/oauth2/bqq/authorize";
    }

    public static String GetLogin_success_prefix() {
        return "http://www.ukuaiqi.com";
    }


    public static final String users_profile = "/users/profile";//用户登录认证成功之后，调用本接口获得详细的当前登录人信息
    public static final String attachments = "/attachment/";//上传附件
    public static final String legwork = "/legwork/";//创建外勤打卡
    public static final String customers = "/customer/";//GET /customers/ 根据条件和分页信息获取客户表数据
    public static final String saleactivities = "/saleactivities/";///saleactivities/{custId} 根据客户Id获取销售动态,返回数组
    public static final String tags = "/tags/";//GET /tags/ 标签
    public static final String contacts = "/contacts/";// get /contacts/{custId} 罗列客户的联系人
    public static final String demands = "/demands/";//  POST /demands/{customerId} 新增购买意向
    public static final String demands_customer = "/demands/customer/";//  GET /demands/customer/{id} 根据客户Id获取客户购买意向列表数据
    public static final String products = "/products/";//GET /products/ 罗列产品,返回数组
    public static final String salestages = "/salestages/";// GET /salestages/ 获取所有销售阶段,返回数组
    public static final String tasks = "/tasks/";// GET /tasks/ 根据条件和分页信息获取任务列表数据
    public static final String workreports = "/workreports/";// DELETE /workreports/{id} 根据 Id 删除工作报告
    public static final String wfinstance = "/wfinstance/";//GET /wfinstance/ 获取当前登录人提交的审批申请
    public static final String bizform = "/bizform/";// GET /bizform/ 查询业务表单
    public static final String wftpl_bizformId = "/wftpl/bizformId/";//get  /wftpl/bizformId/{id}   根据业务表单获取多个预设的工作流模版(供提交审批选择使用),比如请假单可以设置多个流程模板(事假，年假，病假)，
    public static final String password = "/users/setpasswd";

    //意见反馈
    public static final String URL_FEEDBACK = Config_project.SERVER_URL_LOGIN().concat("/api/v2/");
    //检查更新
    public static final String URL_CHECK_UPDATE = Config_project.SERVER_URL_LOGIN().concat("/oapi/app/");
    //修改密码
    public static final String URL_UPDATE_PASSWORD = Config_project.SERVER_URL_LOGIN().concat("/api/v2/user/changepwd/");
    //获取验证码
    public static final String URL_GET_CODE = Config_project.SERVER_URL_LOGIN().concat("/oapi/sms/code/");
    //忘记密码时候，获取验证码
    public static final String URL_GET_PWDCODE = Config_project.SERVER_URL_LOGIN().concat("/oapi/sms/forgetpwd");
    //修改手机号
    public static final String URL_VERIFY_PHONE = Config_project.SERVER_URL_LOGIN().concat("/oapi/sms/");
    //重置密码
    public static final String URL_VERIFY_CODE = Config_project.SERVER_URL_LOGIN().concat("/oapi/sms/code/verify/");
    public static final String URL_RESET_PASSWORD = Config_project.SERVER_URL_LOGIN().concat("/oapi/auth/setpwd/");


    //客户相关
    /**
     * 个人附近客户数量
     **/
    public static final String QUERY_NEAR_CUSTOMERS_COUNT_SELF = Config_project.API_URL_CUSTOMER() + "/customer/selfcount";
    /**
     * 团队附近客户数量
     **/
    public static final String QUERY_NEAR_CUSTOMERS_COUNT_TEAM = Config_project.API_URL_CUSTOMER() + "/customer/teamcount";
    /**
     * 个人附近客户数集
     **/
    public static final String QUERY_NEAR_CUSTOMERS_SELF = Config_project.API_URL_CUSTOMER() + "/customer/selfnear";
    /**
     * 团队附近客户数集
     **/
    public static final String QUERY_NEAR_CUSTOMERS_TEAM = Config_project.API_URL_CUSTOMER() + "/customer/teamnear";
    /**
     * 公司已赢单客户数集
     **/
    public static final String QUERY_CUSTOMERS_COMPANY = Config_project.API_URL_CUSTOMER() + "/customer/winnear";
    /**
     * 个人客户数据集
     **/
    public static final String QUERY_CUSTOMERS_SELF = Config_project.API_URL_CUSTOMER() + "/customer/self";
    /**
     * 团队客户数据集
     **/
    public static final String QUERY_CUSTOMERS_TEAM = Config_project.API_URL_CUSTOMER() + "/customer/team";
    /**
     * 公海客户数据集
     **/
    public static final String QUERY_CUSTOMERS_PUBLIC = Config_project.API_URL_CUSTOMER() + "/customer/shared";
    /**
     * 个人客户查询
     **/
    public static final String SEARCH_CUSTOMERS_SELF = Config_project.API_URL_CUSTOMER() + "/customer/selfsearch";
    /**
     * 团队客户查询
     **/
    public static final String SEARCH_CUSTOMERS_TEAM = Config_project.API_URL_CUSTOMER() + "/customer/teamsearch";
    /**
     * 公海客户查询
     **/
    public static final String SEARCH_CUSTOMERS_PUBLIC = Config_project.API_URL_CUSTOMER() + "/customer/sharedsearch";


    /**
     * 登陆
     **/
    public static final String GET_TOKEN = Config_project.SERVER_URL_LOGIN() + "/oapi/auth/";
    /**
     * 获取个人资料
     **/
    public static final String GET_PROFILE = Config_project.SERVER_URL_LOGIN() + "/api/v2/user/profile/";
    /**
     * 更新个人资料
     **/
    public static final String UPDATE_PROFILE = Config_project.SERVER_URL_LOGIN() + "/api/v2/user/{id}profile/";
    /**
     * 获取组织架构
     **/
    public static final String GET_ORGANIZATION = Config_project.SERVER_URL_LOGIN() + "/api/v2/user/organization/";



    /*变量传递*/

    /*缓存信息*/
    //基础缓存
    public static final String BASE_SHARE = "base_share";
    public static final String NUMBER_PWD = "number_pwd"; //数字密码 2015－01－21
    public static final String TOKEN = "token";

    public static final String LAST_TRACKLOG = "last_track_log";
    public static final String LAST_CHECK_TRACKLOG = "last_check_track_log";

    //小米推送
    public static final String XM_APP_ALIAS = "loyosharecloud";
    public static final String XM_APP_ID = "2882303761517371532";
    public static final String XM_APP_KEY = "5681737162532";

    public static final String ACTION_DATA_CHANGE = "com.loyo.oa.v2.ACTION_DATA_CHANGE";

}
