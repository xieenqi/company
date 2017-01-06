package com.loyo.oa.v2.activityui.setting.api;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 设置相关的接口
 * Created by jie on 16/12/21.
 */

public interface ISetting {

    /**********修改手机相关*************/
    @GET("/")
    Observable<Object> getVerifyCode(@Query("tel") String tel);
    /**
     * 跟换手机号 /oapi/sms/code/verify
     */
    @PUT("/user/update/num")
    Observable<BaseBean> modifyMobile(@Body HashMap<String, Object> map);

    @GET("/")
    Observable<Object> verifyCode(@QueryMap HashMap<String, Object> map);

    @GET("/newphonenum/")
    Observable<Object> verifyPhone(@Query("tel") String tel);

    /**
     * 绑定手机号码
     */
    @PUT("/user/bindmobile")
    Observable<Object> bindMobile(@Body HashMap<String,Object> map);

}
