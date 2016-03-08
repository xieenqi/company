package com.loyo.oa.v2.point;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * com.loyo.oa.v2.point
 * 描述 :修改手机号相关接口
 * 作者 : ykb
 * 时间 : 15/8/27.
 */
public interface IMobile {
    @GET("/")
    void getVerifyCode(@Query("tel") String tel, Callback<Object> callback);

    @GET("/oapi/sms/code/verify")
    void modifyMobile(@QueryMap HashMap<String, Object> map, Callback<Object> callback);

    @GET("/")
    void verifyCode(@QueryMap HashMap<String, Object> map, Callback<Object> callback);

    @GET("/newphonenum/")
    void verifyPhone(@Query("tel") String tel, Callback<Object> callback);

    /**
     * 绑定手机号码
     *
     * @param callback
     */
    @PUT("/user/bindmobile")
    void bindMobile(@Body HashMap<String,Object> map, Callback<Object> callback);

}
