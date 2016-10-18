package com.loyo.oa.v2.activityui.login;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.home.MainHomeActivity;
import com.loyo.oa.v2.activityui.login.presenter.LoginBqqPresenter;
import com.loyo.oa.v2.activityui.login.presenter.impl.LoginBqqPresenterImpl;
import com.loyo.oa.v2.activityui.login.viewcontrol.LoginBqqView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;

/**
 * 【企业QQ】登录
 * Restruture by yyy on 16/10/14
 */
public class LoginBQQActivity extends BaseActivity implements LoginBqqView{

    private WebView mWebView;
    private LoginBqqPresenter mPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tintManager.setTintColor(android.R.color.transparent);
        setContentView(R.layout.activity_bqq_login);
        super.isNeedLogin = false;
        initUI();
    }

    /**
     * 页面初始化
     * */
    void initUI() {
        mPresenter = new LoginBqqPresenterImpl(mContext,LoginBQQActivity.this,this);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mPresenter.loadWebView(mWebView);
    }

    /**
     * 跳转主页
     * */
    @Override
    public void startMainHome(String url) {
        //String token = url.substring(FinalVariables.GetLogin_success_prefix().length(), url.length());
        String[] token = url.split("=");
        LogUtil.d("QQ登录token:" + token[1]);
        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.APP_START, "openOne");
        MainApp.setToken(token[1]);
        MainApp.isQQLogin = true;
        SharedUtil.put(mContext, FinalVariables.TOKEN, token[1]);
        MainApp.getMainApp().startActivity(LoginBQQActivity.this, MainHomeActivity.class, MainApp.ENTER_TYPE_BUTTOM, true, new Bundle());
        SharedUtil.putBoolean(mContext, ExtraAndResult.WELCOM_KEY, true);

        /* 拉取组织架构 */
        OrganizationService.startActionFetchAll(MainApp.getMainApp());
    }

    /**
     * 返回监听
     * */
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        LogUtil.d("onKeyDown");
        LogUtil.d("((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()):" + ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()));
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            app.startActivity(this, LoginActivity.class, MainApp.ENTER_TYPE_LEFT, true, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showProgress() {
        showLoading("");
    }

    @Override
    public void hideProgress() {
        cancelLoading();
    }

    @Override
    public void showMsg(String message) {

    }


}
