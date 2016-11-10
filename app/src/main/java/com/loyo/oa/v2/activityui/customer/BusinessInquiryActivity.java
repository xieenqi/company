package com.loyo.oa.v2.activityui.customer;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 【工商查询】 WebView页面  此页面功能比价简单 暂不使用MVP模式
 * Created by xeq on 16/10/31.
 */

public class BusinessInquiryActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar pb_progress;
    private TextView tv_ext;
    //    private TextView tv_title_1;
    private String url = "https://info.camcard.com/site/sdk?";
    private String sdk = "from_sdk=";
    private String user = "&user=mobile_";
    private String search = "&search=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_inquiry);
        try {
            initView();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void initView() throws UnsupportedEncodingException {
        String Keyword = getIntent().getStringExtra("Keyword");
        webView = (WebView) findViewById(R.id.webView);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        tv_ext = (TextView) findViewById(R.id.tv_ext);
        tv_ext.setOnTouchListener(Global.GetTouch());
        tv_ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //        img_title_right = (RelativeLayo、ut) findViewById(R.id.img_title_right);
//        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
//        img_title_right.setVisibility(View.INVISIBLE);
//        tv_title_1.setText("工商查询");//from_sdk=loyo&search=

        url = url + sdk + URLEncoder.encode("loyo", "UTF-8") + search + URLEncoder.encode(Keyword, "UTF-8") + user + getMobile();
        initWebSetting();
    }

    private String getMobile() {
        //用户没有手机号码的时候就拼接一个号码 保证user的唯一性
        if (MainApp.user == null || TextUtils.isEmpty(MainApp.user.mobile)) {
            TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String szImei = TelephonyMgr.getDeviceId();
            return "1818068" + szImei.substring(szImei.length() - 4, szImei.length());
        } else {

            return MainApp.user.mobile;
        }
    }

    private void initWebSetting() {
        if (Build.VERSION.SDK_INT >= 14) {
            webView.shouldDelayChildPressedState();
        }
        webView.setInitialScale(39);// 为25%，最小缩放等级
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings setting = webView.getSettings();
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultFontSize(45);
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);//重要
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 自适应屏幕宽
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;//super.shouldOverrideUrlLoading(view, url);
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
