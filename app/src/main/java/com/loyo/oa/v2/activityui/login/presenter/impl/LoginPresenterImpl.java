package com.loyo.oa.v2.activityui.login.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.activityui.login.api.LoginService;
import com.loyo.oa.v2.activityui.login.model.Token;
import com.loyo.oa.v2.activityui.login.presenter.LoginPresenter;
import com.loyo.oa.v2.activityui.login.viewcontrol.LoginView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.WaveView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.point.ILogin;
import com.loyo.oa.v2.service.OrganizationService;
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
 * 【登录界面】Presenter
 * Created by yyy on 16/10/8.
 */

public class LoginPresenterImpl implements LoginPresenter {

    public LoginView crolView;
    public WaveView mWaveView;
    public Context mContext;

    public LoginPresenterImpl(LoginView loginView, WaveView mWaveView, Context mContext) {
        this.crolView = loginView;
        this.mWaveView = mWaveView;
        this.mContext = mContext;
    }

    public Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                changeColor(R.color.title_bg1, R.color.red1);
                mWaveView.setText("登录失败");
            }
        }
    };

    /**
     * 登录请求
     */
    @Override
    public void requestLogin(HashMap<String, Object> body) {
//        RestAdapter adapter = new RestAdapter.Builder()
//                .setEndpoint(FinalVariables.GET_TOKEN) //URL
//                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
//                .build();
//        adapter.create(ILogin.class).login(body, new RCallback<BaseBeanT<String>>() {
//            @Override
//            public void success(final BaseBeanT<String> token, final Response response) {
//                HttpErrorCheck.checkResponse("登录: ", response);
//                if (null == token || token.data == null || TextUtils.isEmpty(token.data)) {
//                    crolView.onError();
//                    return;
//                } else {
//                    crolView.onSuccess(token.data);
//                }
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                super.failure(error);
//                HttpErrorCheck.checkError(error);
//                crolView.onError();
//            }
//        });

        LoginService.login(body).subscribe(new DefaultLoyoSubscriber<BaseBeanT<String>>() {
            @Override
            public void onNext(BaseBeanT<String> token) {
                if (null == token || token.data == null || TextUtils.isEmpty(token.data)) {
                    crolView.onError();
                    return;
                } else {
                    crolView.onSuccess(token.data);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                crolView.onError();
            }
        });
    }

    /**
     * 登录成功后操作
     */
    @Override
    public void onSuccessEmbl(final String token, final Activity mActivity) {
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
                MainApp.setToken(token);
                SharedUtil.put(mContext, FinalVariables.TOKEN, token);
                SharedUtil.putBoolean(mContext, ExtraAndResult.WELCOM_KEY, true);//预览过引导页面内
                MainApp.getMainApp().startActivity(mActivity, MainHomeActivity.class, MainApp.ENTER_TYPE_RIGHT, true, new Bundle());
                SharedUtil.putBoolean(mContext, ExtraAndResult.WELCOM_KEY, true);

                /* 拉取组织架构 */
                OrganizationService.startActionFetchAll(MainApp.getMainApp());
            }
        });
    }

    /**
     * 登录失败后操作
     */
    @Override
    public void onErrorEmbl() {
        mHandler.sendEmptyMessage(0x01);
    }

    /**
     * 输入框监听
     */
    @Override
    public void editTextListner(EditText edt_username, EditText edt_password) {
        edt_username.addTextChangedListener(nameWatcher);
        edt_password.addTextChangedListener(nameWatcher);
    }

    /**
     * 按钮颜色改变
     */
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
     */
    @Override
    public void requestStandBy(String username, String password) {
        if (StringUtil.isEmpty(username)) {
            crolView.verifyError(1);
            return;
        }
        if (StringUtil.isEmpty(password)) {
            crolView.verifyError(2);
            return;
        }
        crolView.verifySuccess();
    }


    public TextWatcher nameWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            crolView.editTextListener();
        }
    };
}


