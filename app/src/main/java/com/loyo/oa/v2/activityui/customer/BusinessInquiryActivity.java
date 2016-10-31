package com.loyo.oa.v2.activityui.customer;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 【工商查询】 WebView页面
 * Created by xeq on 16/10/31.
 */

public class BusinessInquiryActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar pb_progress;
    private RelativeLayout img_title_left, img_title_right;
    private TextView tv_title_1;
    private String url = "https://info.camcard.com/site/sdk?";
    private String sdk = "from_sdk=";
    private String user = "&user=email_lihuan@360loyo.com";
    private String search = "&search=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_inquiry);
        try {
            initView();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtil.d("加载一次" + e.toString());
        }
    }

    private void initView() throws UnsupportedEncodingException {
        webView = (WebView) findViewById(R.id.webView);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
//        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
//        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
//        img_title_right.setVisibility(View.INVISIBLE);
//        img_title_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        tv_title_1.setText("工商查询");//from_sdk=loyo&search=

        url = url + sdk + URLEncoder.encode("loyo", "UTF-8") + search + URLEncoder.encode("成都乐育", "UTF-8") + user;
        initWebSetting();
    }

    private void initWebSetting() {
//        if (Build.VERSION.SDK_INT >= 14) {
//            webView.shouldDelayChildPressedState();
//        }
        webView.setInitialScale(39);// 为25%，最小缩放等级
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings setting = webView.getSettings();
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultFontSize(45);
        setting.setJavaScriptEnabled(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 自适应屏幕宽

//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
//        WebSettings set = webView.getSettings();
//        set.setSavePassword(false);
//        set.setSaveFormData(false);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeAllCookie();
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    pb_progress.setVisibility(View.GONE);
                } else {
                    pb_progress.setVisibility(View.VISIBLE);
                    pb_progress.setProgress(newProgress);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.d("Start加载url:" + url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.d("加载url:" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                LogUtil.d("加载错误:" + errorCode + "  " + description + "  " + failingUrl);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
        webView.loadUrl(url);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
