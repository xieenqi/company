package com.loyo.oa.v2.activityui.login.api;

import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.beans.BaseBeanT;

import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import rx.Observable;

/**
 * 登陆模块网络接口
 * Created by jie on 16/12/21.
 */

public interface ILogin {
    /**
     * 登陆
     */
    @POST("/")
    Observable<BaseBeanT<String>> login(@Body HashMap<String, Object> body);

    /**
     * 没有地方使用过
     * 更新unionid
     */
    @PUT("/users/bindingWX")
    Observable<User> updateWxUnionId(@Body HashMap<String, Object> body);


    @GET("/newtoken")
    Observable<Token> getNewToken();

}
