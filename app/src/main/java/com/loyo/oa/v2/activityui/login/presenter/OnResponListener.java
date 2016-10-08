package com.loyo.oa.v2.activityui.login.presenter;

import android.app.Activity;
import com.loyo.oa.v2.activityui.login.bean.Token;

/**
 * Created by yyy on 16/10/8.
 */

public interface OnResponListener {

    void onSuccess(Token token, Activity mActivity);
    void onError();

}
