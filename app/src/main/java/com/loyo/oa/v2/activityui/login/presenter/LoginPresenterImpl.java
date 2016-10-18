package com.loyo.oa.v2.activityui.login.presenter;

import android.app.Activity;

import com.loyo.oa.v2.activityui.login.model.Token;

import java.util.HashMap;

/**
 * Created by yyy on 16/10/8.
 */

public interface LoginPresenterImpl {

    /*登录请求*/
    void requestLogin(final HashMap<String, Object> body);

    /*按钮变色*/
    void changeColor(final int bgColor, final int waveColor);

    /*登录验证*/
    void requestStandBy(String username,String password);

    /*登录请求成功操作*/
    void onSuccessEmbl(Token token, Activity mActivity);

    /*登录请求失败操作*/
    void onErrorEmbl();
}
