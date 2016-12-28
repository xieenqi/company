package com.loyo.oa.v2.activityui.home.api;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.activityui.setting.bean.SystemMessageItem;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.ServerTime;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;

/**
 *
 * 首页的网络服务
 * Created by jie on 16/12/21.
 */

public class HomeService {

    public static Observable<ArrayList<HttpMainRedDot>> getNumber() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.MAIN_RED_DOT)
                        .create(IHome.class)
                        .getNumber()
                        .compose(RetrofitAdapterFactory.<ArrayList<HttpMainRedDot>>compatApplySchedulers());
    }

    public static Observable<CheckUpdateService.UpdateInfo> checkUpdate() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_CHECK_UPDATE)
                        .create(IHome.class)
                        .checkUpdate()
                        .compose(RetrofitAdapterFactory.<CheckUpdateService.UpdateInfo>compatApplySchedulers());
    }

    //这个接口没有地方使用
//    public static Observable<ServerTime> getServerTime() {
//        return
//                RetrofitAdapterFactory.getInstance()
//                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
//                        .create(IHome.class)
//                        .getServerTime()
//                        .compose(RetrofitAdapterFactory.<ServerTime>compatApplySchedulers());
//    }

    public static Observable<Object> resetPassword(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_RESET_PASSWORD)
                        .create(IHome.class)
                        .resetPassword(map)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    public static Observable<Object> verifyPhone(String tel) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_VERIFY_PHONE)
                        .create(IHome.class)
                        .verifyPhone(tel)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    //绑定手机获取验证码
    public static Observable<Object> getVerificationCode(String tel) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.GET_VERIFICATION_CODE)
                        .create(IHome.class)
                        .getVerificationCode(tel)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
    /**
     * 验证密码
     */
    public static Observable<BaseBean> verifyPasseord(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.BIND_MOBLIE)
                        .create(IHome.class)
                        .verifyPasseord(map)
                        .compose(RetrofitAdapterFactory.<BaseBean>compatApplySchedulers());
    }

    public static Observable<PaginationX<SystemMessageItem>> getSystemMessage(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IHome.class)
                        .getSystemMessage(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<SystemMessageItem>>compatApplySchedulers());
    }
    /**
     * 读取系统消息的一条
     */
    public static Observable<Object> readSystemMessageOne( String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IHome.class)
                        .readSystemMessageOne(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
    /**
     * 读取系统消息的全部
     */
    public static Observable<Object> readSystemMessageAll() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IHome.class)
                        .readSystemMessageAll()
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
    /**
     * 调用接口回传给服务器跟新系统消息的红点状态
     */
    public static Observable<Object> refreshSystemMessageRed( String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(IHome.class)
                        .refreshSystemMessageRed(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }



}
