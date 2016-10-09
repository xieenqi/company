package com.loyo.oa.v2.activityui.login.modle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.activityui.login.view.OnResponListener;
import com.loyo.oa.v2.activityui.login.bean.Token;
import com.loyo.oa.v2.activityui.login.view.LoginView;
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

public class LoginModleImpl implements LoginModle{

    public Context mContext;
    public WaveView mWaveView;

    public LoginModleImpl(Context mContext,WaveView mWaveView){
        this.mContext = mContext;
        this.mWaveView = mWaveView;
    }

    /**
     * 请求登录操作
     * */
    @Override
    public void login(final HashMap<String, Object> body,final OnResponListener listener,final Activity mActivity) {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(FinalVariables.GET_TOKEN) //URL
                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
                .build();
        adapter.create(ILogin.class).login(body, new RCallback<Token>() {
            @Override
            public void success(final Token token, final Response response) {
                HttpErrorCheck.checkResponse(response);
                if (null == token || TextUtils.isEmpty(token.access_token)) {
                    listener.onError();
                    return;
                } else {
                    listener.onSuccess(token,mActivity);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                listener.onError();
            }
        });
    }

    /**
     * 请求成功 业务处理
     * */
    @Override
    public void successEmbel(final Token token,final Activity mActivity) {
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
     * 请求失败 业务处理
     * */
    @Override
    public void errorEmnel() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                changeColor(R.color.title_bg1, R.color.red1);
                mWaveView.setText("登录失败");
            }
        });
    }

    /**
     * 改变按钮状态
     * */
    @Override
    public void changeColorEmbel(int bgColor, int waveColor) {
        changeColor(bgColor,waveColor);
    }

    /**
     * 账号密码验证
     * */
    @Override
    public void requestStandByEmbel(String username, String password, LoginView mLoginView) {
        if (StringUtil.isEmpty(username)) {
            mLoginView.accountError();
            return;
        }
        if (StringUtil.isEmpty(password)) {
            mLoginView.pwdError();
            return;
        }
        mLoginView.verifyPass();
    }


    /**
     * 改变波纹颜色和按钮颜色
     *
     * @param bgColor
     * @param waveColor
     */
    private void changeColor(final int bgColor, final int waveColor) {
        if (bgColor > 0)
            mWaveView.setBackGroundColor(mContext.getResources().getColor(bgColor));
        if (waveColor > 0)
            mWaveView.setWaveColor(mContext.getResources().getColor(waveColor));
        mWaveView.setMode(WaveView.WAVE_MODE_SPREAD);
        mWaveView.setChangeColor(true);
        mWaveView.startDraw();
    }

}
