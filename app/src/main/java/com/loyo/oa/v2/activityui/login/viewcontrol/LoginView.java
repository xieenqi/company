package com.loyo.oa.v2.activityui.login.viewcontrol;


import com.loyo.oa.v2.activityui.login.model.Token;

/**
 * Created by yyy on 16/10/8.
 */

public interface LoginView {

    /*格式验证成功*/
    void verifySuccess();

    /*格式验证失败*/
    void verifyError(int code);

    /*请求成功*/
    void onSuccess(Token token);

    /*请求失败*/
    void onError();

    /*输入框监听*/
    void editTextListener();

}
