package com.loyo.oa.v2.activity.login;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.MainActivity_;
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
    ProgressBar pb_progress;

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
        showLoading("加载中...");
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBlockNetworkImage(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final boolean blockLoadingNetworkImage = true;
                LogUtil.d("QQ登录URL:" + url);
                if (url.indexOf(FinalVariables.GetLogin_success_prefix()) >= 0) {
                    //String token = url.substring(FinalVariables.GetLogin_success_prefix().length(), url.length());
                    String[] token = url.split("=");
                    LogUtil.d("QQ登录token:" + token[1]);
                    MainApp.setToken(token[1]);
                    MainApp.isQQLogin = true;
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
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb_progress.setProgress(newProgress);
                if (newProgress == 100) {
                    pb_progress.setVisibility(View.GONE);
                    cancelLoading();
                }
                super.onProgressChanged(view, newProgress);
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
