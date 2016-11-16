package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.ServerTime;
import com.loyo.oa.v2.service.CheckUpdateService;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface IMain {

    @GET("/feed")
    void getNumber(retrofit.Callback<ArrayList<HttpMainRedDot>> cb);

    @GET("/checkupdate/android")
    void checkUpdate(retrofit.Callback<CheckUpdateService.UpdateInfo> cb);

    @GET("/gettime")
    void getServerTime(retrofit.Callback<ServerTime> cb);

    @PUT("/")
    void resetPassword(@Body HashMap<String, Object> map, retrofit.Callback<Object> cb);

    @GET("/forgetpwd/")
    void verifyPhone(@Query("tel") String tel, Callback<Object> callback);

    //绑定手机获取验证码
    @GET("/newphonenum")
    void getVerificationCode(@Query("tel") String tel, Callback<Object> callback);

    /**
     * 验证密码
     *
     * @param map
     * @param callback
     */
    @POST("/user/password/verify")
    void verifyPasseord(@Body HashMap<String, Object> map, Callback<BaseBean> callback);

    @GET("/message")
    void getSystemMessage(@QueryMap HashMap<String, Object> map, Callback<PaginationX<SystemMessageItem>> callback);

    /**
     * 读取系统消息的一条
     */
    @PUT("/message/read/{id}")
    void readSystemMessageOne(@Path("id") String id, Callback<Object> o);

    /**
     * 读取系统消息的全部
     */
    @PUT("/message/read/all")
    void readSystemMessageAll(Callback<Object> o);

    /**
     * 调用接口回传给服务器跟新系统消息的红点状态
     */
    @PUT("/message/read/pusher/{id}")
    void refreshSystemMessageRed(@Path("id") String id, Callback<Object> o);
}
