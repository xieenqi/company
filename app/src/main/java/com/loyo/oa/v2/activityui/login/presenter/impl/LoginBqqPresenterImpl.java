package com.loyo.oa.v2.activityui.login.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.loyo.oa.v2.activityui.login.presenter.LoginBqqPresenter;
import com.loyo.oa.v2.activityui.login.viewcontrol.LoginBqqView;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * 【企业QQ登录】Presenter
 * Created by yyy on 16/10/14.
 */

public class LoginBqqPresenterImpl implements LoginBqqPresenter{

    public Context mContext;
    public Activity mActivity;
    public LoginBqqView crolView;

    public LoginBqqPresenterImpl(Context mContext,Activity mActivity,LoginBqqView crolView){
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.crolView  = crolView;
    }

    /**
     * 加载WebView
     * */
    @Override
    public void loadWebView(final WebView mWebView) {
        crolView.showProgress("");
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                final boolean blockLoadingNetworkImage = true;
                LogUtil.d("QQ登录URL:" + url);
                if (url.indexOf(FinalVariables.GetLogin_success_prefix()) >= 0) {
                    crolView.startMainHome(url);
                } else {
                    view.loadUrl(url);
                }
                if (blockLoadingNetworkImage) {
                    mWebView.getSettings().setBlockNetworkImage(false);
                }
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(final WebView view, final int newProgress) {
                if (newProgress == 100) {
                    crolView.hideProgress();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        WebSettings set = mWebView.getSettings();
        set.setSavePassword(false);
        set.setSaveFormData(false);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        mWebView.loadUrl(FinalVariables.GetBQQLogin());
    }
}
