package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.ServerTime;
import com.loyo.oa.v2.service.CheckUpdateService;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

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

    @POST("/user/password/verify")
    void verifyPasseord(@Body HashMap<String, Object> map, Callback<BaseBean> callback);
}
