package com.loyo.oa.v2.activityui.login.presenter;

import android.app.Activity;
import android.content.Context;

import com.loyo.oa.v2.activityui.login.bean.Token;
import com.loyo.oa.v2.activityui.login.modle.LoginModleImpl;
import com.loyo.oa.v2.activityui.login.modle.LoginModle;
import com.loyo.oa.v2.activityui.login.view.LoginView;
import com.loyo.oa.v2.activityui.login.view.OnResponListener;
import com.loyo.oa.v2.customview.WaveView;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/8.
 */

public class LoginPresenterImpl implements OnResponListener,LoginPresenter{

    public LoginModle mLoginModle;
    public LoginView mLoginView;
    public WaveView mWaveView;
    public Context mContext;

    public LoginPresenterImpl(LoginView loginView,WaveView mWaveView,Context mContext){
        this.mLoginView = loginView;
        this.mWaveView = mWaveView;
        this.mContext  = mContext;
        mLoginModle = new LoginModleImpl(mContext,mWaveView);
    }

    @Override
    public void requestLogin(HashMap<String, Object> body,Activity mActivity) {
        mLoginModle.login(body,this,mActivity);
    }


    @Override
    public void onSuccess(Token token,Activity mActivity) {
        mLoginModle.successEmbel(token,mActivity);

    }

    @Override
    public void onError() {
        mLoginModle.errorEmnel();
    }


    @Override
    public void changeColor(int bgColor, int waveColor) {
        mLoginModle.changeColorEmbel(bgColor,waveColor);
    }

    @Override
    public void requestStandBy(String username,String password) {
        mLoginModle.requestStandByEmbel(username,password, mLoginView);
    }
}
