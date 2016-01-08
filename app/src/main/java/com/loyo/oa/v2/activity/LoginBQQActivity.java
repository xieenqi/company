package com.loyo.oa.v2.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;

/**
 * 企业qq网页登陆
 */
public class LoginBQQActivity extends BaseActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bqq_login);
        super.isNeedLogin = false;
        initUI();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        app.logUtil.d("onKeyDown");
        app.logUtil.d("((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()):" + ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()));
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            app.startActivity(this, LoginActivity.class, MainApp.ENTER_TYPE_LEFT, true, null);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void initUI() {
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final boolean blockLoadingNetworkImage = true;
                LogUtil.dll("URL:" + url);
                if (url.indexOf(FinalVariables.GetLogin_success_prefix()) >= 0) {
                    //String token = url.substring(FinalVariables.GetLogin_success_prefix().length(), url.length());
                    String[] token = url.split("=");
                    LogUtil.dll("token:" + token);
                    MainApp.setToken(token[1]);
                    SharedUtil.put(mContext, FinalVariables.TOKEN, token[1]);
                    app.startActivity(LoginBQQActivity.this, MainActivity_.class, MainApp.ENTER_TYPE_BUTTOM, true, new Bundle());
                } else {
                    view.loadUrl(url);
                }
                if (blockLoadingNetworkImage) {
                    webView.getSettings().setBlockNetworkImage(false);
                }
                return true;
            }
        });

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        WebSettings set = webView.getSettings();
        set.setSavePassword(false);
        set.setSaveFormData(false);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        webView.loadUrl(FinalVariables.GetBQQLogin());
    }

}
