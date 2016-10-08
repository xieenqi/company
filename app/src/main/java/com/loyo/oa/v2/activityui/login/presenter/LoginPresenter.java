package com.loyo.oa.v2.activityui.login.presenter;

import android.app.Activity;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/8.
 */

public interface LoginPresenter {

    void requestLogin(final HashMap<String, Object> body, Activity mActivity);

    void changeColor(final int bgColor, final int waveColor);

    void requestStandBy(String username,String password);

}
