package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.customview.ProgressWebView;

import java.net.URLEncoder;

public class PreviewOfficeActivity extends Activity {

    ProgressWebView mWebView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_preview);

        mWebView = (ProgressWebView) findViewById(R.id.webView);

        if (getIntent().hasExtra("data")) {
            String fileUrl = getIntent().getStringExtra("data");

            if (!StringUtil.isEmpty(fileUrl) && Global.IsOffice(fileUrl)) {

                try {
                    String url = "https://view.officeapps.live.com/op/view.aspx?src=" + URLEncoder.encode(fileUrl, "UTF-8");
                    mWebView.getSettings().setJavaScriptEnabled(true);

                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
                            handler.proceed();
                        }
                    });
                    LogUtil.d(" web显示图片URL："+url);
                    mWebView.loadUrl(url);
                } catch (Exception e) {
                    Global.ProcException(e);
                }
            } else {
                Global.Toast("格式不支持!");
                onBackPressed();
            }
        }
    }

}
