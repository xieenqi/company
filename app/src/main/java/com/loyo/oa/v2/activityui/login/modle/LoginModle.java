package com.loyo.oa.v2.activityui.login.modle;

import android.app.Activity;
import com.loyo.oa.v2.activityui.login.view.OnResponListener;
import com.loyo.oa.v2.activityui.login.bean.Token;
import com.loyo.oa.v2.activityui.login.view.LoginView;

import java.util.HashMap;

/**
 * Created by yyy on 16/10/8.
 */

public interface LoginModle {

    void login(HashMap<String, Object> body, OnResponListener listener, Activity mActivity);

    void successEmbel(Token token, Activity mActivity);

    void errorEmnel();

    void changeColorEmbel(int bgColor, int waveColor);

    void requestStandByEmbel(String username, String password, LoginView mLoginView);

}
