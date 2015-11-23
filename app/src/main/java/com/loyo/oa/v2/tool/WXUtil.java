package com.loyo.oa.v2.tool;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loyo.oa.v2.BuildConfig;
import com.loyo.oa.v2.application.MainApp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.URLEncoder;

/**
 * com.loyo.oa.v2.tool
 * 描述 :微信工具
 * 作者 : ykb
 * 时间 : 15/7/31.
 */
public class WXUtil
{
    /**超时时间 s**/
    private static final int CONNECT_TIME_OUT=5000;
    /**获得微信授权码的广播**/
    public static final String ACTION_WX_CODE_RETURN="com.loyo.oa.v2.action_wx_code_return";

    /**微信APPID**/
    public static final String WX_APPID= BuildConfig.DEBUG?"wxfe70ef7a22b38334":"wx95b53de63eca03aa";
    /**微信APPSECRET**/
    public static final String WX_APPSECRET=BuildConfig.DEBUG?"eeb458bbc225304326c918594ce81632":"cfcb59b529e233552154160942db4e2b";
    /**微信授权作用域**/
    public final static String WEIXIN_SCOPE = "snsapi_userinfo";
    /**自定义标识**/
    public final static String WEIXIN_STATE =  BuildConfig.DEBUG?"loyo_3.1.2.debug":"loyo_3.1.2";

    /**微信授权token获取地址**/
    public static String WX_GET_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    /**微信获取用户个人信息地址**/
    public static String WX_GET_USER_INFO_URL="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";

    /**微信openid**/
    public static  String wxOpenId;
    /**微信token**/
    public static String wxToken;
    /**微信授权码**/
    public static String wxCode;
    /**用户微信昵称**/
    public static String wxUserNickName;
    /**用户微信头像地址**/
    public static String wxUserHeadImgUrl;
    /*微信用户唯一标识**/
    public static String wxUnionid;


    private IWXAPI iwxapi;
    private static volatile WXUtil util;
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(CONNECT_TIME_OUT); // 设置链接超时，如果不设置，默认为10s
    }


    private WXUtil(){

    }

    public static WXUtil getInstance()
    {
        synchronized (WXUtil.class) {
            if (null == util)
                util = new WXUtil();
        }
        return util;
    }

    /**
     * 获取微信API
     * @return
     */
    public IWXAPI getIwxapi(){
        if(null==iwxapi)
            iwxapi= WXAPIFactory.createWXAPI(MainApp.getMainApp(), WX_APPID, false);

        return  iwxapi;
    }


    /**
     * 获取access_token的URL（微信）
     * @param code 授权时，微信回调给的
     * @return URL
     */
    private String getCodeRequestUrl(String code) {
        String result = WX_GET_TOKEN_URL;
        result = result.replace("APPID",
                urlEnodeUTF8(WX_APPID));
        result = result.replace("SECRET",
                urlEnodeUTF8(WX_APPSECRET));
        result = result.replace("CODE",urlEnodeUTF8(code));
        return result;
    }

    /**
     * 获取用户个人信息的URL（微信）
     * @param access_token 获取access_token时给的
     * @param openid 获取access_token时给的
     * @return URL
     */
    private String getUserInfoUrl(String access_token,String openid){
        String result = WX_GET_USER_INFO_URL;
        result = result.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        result = result.replace("OPENID",
                urlEnodeUTF8(openid));
        return result;
    }

    /**
     * 转换为UTF-8格式字符串
     * @param str
     * @return
     */
    private  String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取access_token等等的信息(微信)
     * @param res token、openId回调接口
     */
    public void getWxAccessToken(AsyncHttpResponseHandler res){
        client.post(getCodeRequestUrl(wxCode), res);
    }

    /**
     * 获取微信用户个人信息
     * @param res 用户信息回调接口
     */
    public void getWxUserInfo(AsyncHttpResponseHandler res){
        client.get(getCodeRequestUrl(getUserInfoUrl(wxToken,wxOpenId)), res);
    }

}
