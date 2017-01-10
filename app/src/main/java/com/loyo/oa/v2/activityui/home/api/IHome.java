package com.loyo.oa.v2.activityui.home.api;

import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.ServerTime;
import com.loyo.oa.v2.network.model.BaseResponse;
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
import rx.Observable;

/**
 * 首页的网络接口
 * Created by jie on 16/12/21.
 */

public interface IHome {

    @GET("/feed")
    Observable<ArrayList<HttpMainRedDot>> getNumber();

    //首页订单待处理数量过获取
    @GET("/worksheets/self/count")
    Observable<BaseResponse<Integer>> getWorksheetNumber();

    @GET("/checkupdate/android")
    Observable<CheckUpdateService.UpdateInfo> checkUpdate();

    @GET("/gettime")
    Observable<ServerTime> getServerTime();

    @PUT("/")
    Observable<Object> resetPassword(@Body HashMap<String, Object> map);

    @GET("/forgetpwd/")
    Observable<Object>  verifyPhone(@Query("tel") String tel);

    //绑定手机获取验证码
    @GET("/newphonenum")
    Observable<Object>  getVerificationCode(@Query("tel") String tel);

    /**
     * 验证密码
     */
    @POST("/user/password/verify")
    Observable<BaseBean> verifyPasseord(@Body HashMap<String, Object> map);

    @GET("/message")
    Observable<PaginationX<SystemMessageItem>> getSystemMessage(@QueryMap HashMap<String, Object> map);

    /**
     * 读取系统消息的一条
     */
    @PUT("/message/read/{id}")
    Observable<Object> readSystemMessageOne(@Path("id") String id);

    /**
     * 读取系统消息的全部
     */
    @PUT("/message/read/all")
    Observable<Object> readSystemMessageAll();

    /**
     * 调用接口回传给服务器跟新系统消息的红点状态
     */
    @PUT("/message/read/pusher/{id}")
    Observable<Object> refreshSystemMessageRed(@Path("id") String id);

}
