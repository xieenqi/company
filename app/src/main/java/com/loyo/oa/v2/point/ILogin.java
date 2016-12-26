package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.beans.BaseBeanT;

import java.util.HashMap;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * com.loyo.oa.v2.point
 * 描述 :登陆接口
 * 作者 : ykb
 * 时间 : 15/8/4.
 */
public interface ILogin {

    /**
     * 登陆
     *
     * @param body 表单
     * @param cb   返回对象
     */
    @POST("/")
    void login(@Body HashMap<String, Object> body, retrofit.Callback<BaseBeanT<String>> cb);

    /**
     * 更新unionid
     *
     * @param body 表单
     * @param cb   返回对象
     */
    @PUT("/users/bindingWX")
    void updateWxUnionId(@Body HashMap<String, Object> body, retrofit.Callback<User> cb);


    @GET("/newtoken")
    void getNewToken(retrofit.Callback<Token> back);

}
