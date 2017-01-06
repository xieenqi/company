package com.loyo.oa.v2.activityui.setting.api;

import com.loyo.oa.v2.activityui.login.api.ILogin;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * 设置相关的接口，目前主要是手机号码的更换
 * Created by jie on 16/12/21.
 */

public class SettingService {
    //获取验证码
    public static Observable<Object> getVerifyCode(String tel) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_GET_CODE)
                        .create(ISetting.class)
                        .getVerifyCode(tel)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    //跟换手机号 /oapi/sms/code/verify
    public static Observable<BaseBean> modifyMobile(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.MAIN_RED_DOT)
                        .create(ISetting.class)
                        .modifyMobile(map)
                        .compose(RetrofitAdapterFactory.<BaseBean>compatApplySchedulers());
    }

    //验证 验证码
    public static Observable<Object> verifyCode(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_VERIFY_CODE)
                        .create(ISetting.class)
                        .verifyCode(map)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
    //验证 手机号码
    public static Observable<Object> verifyPhone( String tel) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.URL_VERIFY_PHONE)
                        .create(ISetting.class)
                        .verifyPhone(tel)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    //绑定手机号码
    public static Observable<Object> bindMobile(HashMap<String,Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.BIND_MOBLIE)
                        .create(ISetting.class)
                        .bindMobile(map)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }


}
