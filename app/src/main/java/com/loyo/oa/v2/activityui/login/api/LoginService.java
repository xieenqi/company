package com.loyo.oa.v2.activityui.login.api;

import com.loyo.oa.v2.activityui.discuss.api.IDiscuss;
import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;

import rx.Observable;

/**
 * 登陆的网络服务
 * Created by jie on 16/12/21.
 */

public class LoginService {
    //登陆
    public static Observable<BaseBeanT<String>> login(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.GET_TOKEN)
                        .create(ILogin.class)
                        .login(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<String>>compatApplySchedulers());
    }

    public static Observable<Token> getNewToken() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.GET_TOKEN)
                        .create(ILogin.class)
                        .getNewToken()
                        .compose(RetrofitAdapterFactory.<Token>compatApplySchedulers());
    }

}
