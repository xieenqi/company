package com.loyo.oa.v2.activityui.login.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.activityui.login.viewcontrol.LoginView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.WaveView;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.StringUtil;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yyy on 16/10/8.
 */

public class LoginPresenter implements LoginPresenterImpl {

    public LoginView mLoginView;
    public WaveView mWaveView;
    public Context mContext;

    public LoginPresenter(LoginView loginView, WaveView mWaveView, Context mContext){
        this.mLoginView = loginView;
        this.mWaveView = mWaveView;
        this.mContext  = mContext;
    }

    public Handler mHandler = new Handler(){

        public void handleMessage(Message msg){
            if(msg.what == 0x01){
                changeColor(R.color.title_bg1, R.color.red1);
                mWaveView.setText("登录失败");
            }
        }
    };

    /**
     * 登录请求
     * */
    @Override
    public void requestLogin(HashMap<String, Object> body) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(FinalVariables.GET_TOKEN) //URL
                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
                .build();
        adapter.create(ILogin.class).login(body, new RCallback<Token>() {
            @Override
            public void success(final Token token, final Response response) {
                HttpErrorCheck.checkResponse(response);
                if (null == token || TextUtils.isEmpty(token.access_token)) {
                    mLoginView.onError();
                    return;
                } else {
                    mLoginView.onSuccess(token);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                mLoginView.onError();
            }
        });
    }

    /**
     * 登录成功后操作
     * */
    @Override
    public void onSuccessEmbl(final Token token, final Activity mActivity) {
        mWaveView.setText("登录成功");
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.APP_START, "openOne");
                //登录成功
                MainApp.setToken(token.access_token);
                SharedUtil.put(mContext, FinalVariables.TOKEN, token.access_token);
                SharedUtil.putBoolean(mContext, ExtraAndResult.WELCOM_KEY, true);//预览过引导页面内
                MainApp.getMainApp().startActivity(mActivity, MainHomeActivity.class, MainApp.ENTER_TYPE_RIGHT, true, new Bundle());
                SharedUtil.putBoolean(mContext, ExtraAndResult.WELCOM_KEY, true);
            }
        });

    }

    /**
     * 登录失败后操作
     * */
    @Override
    public void onErrorEmbl() {
        mHandler.sendEmptyMessage(0x01);
    }

    /**
     * 按钮颜色改变
     * */
    @Override
    public void changeColor(int bgColor, int waveColor) {
        if (bgColor > 0)
            mWaveView.setBackGroundColor(mContext.getResources().getColor(bgColor));
        if (waveColor > 0)
            mWaveView.setWaveColor(mContext.getResources().getColor(waveColor));
        mWaveView.setMode(WaveView.WAVE_MODE_SPREAD);
        mWaveView.setChangeColor(true);
        mWaveView.startDraw();
    }

    /**
     * 登录请求验证
     * */
    @Override
    public void requestStandBy(String username,String password) {
        if (StringUtil.isEmpty(username)) {
            mLoginView.verifyError(1);
            return;
        }
        if (StringUtil.isEmpty(password)) {
            mLoginView.verifyError(2);
            return;
        }
        mLoginView.verifySuccess();
    }
}
