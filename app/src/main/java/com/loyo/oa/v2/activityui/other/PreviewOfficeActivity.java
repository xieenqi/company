package com.loyo.oa.v2.activityui.other;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ProgressWebView;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;

import java.net.URLEncoder;

/**
 * 预览 Word文档
 */
public class PreviewOfficeActivity extends Activity {

    ProgressWebView mWebView;
    ProgressBar pb_progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_preview);

        mWebView = (ProgressWebView) findViewById(R.id.webView);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
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
                    LogUtil.d(" web显示图片URL：" + url);
                    mWebView.loadUrl(url);
                } catch (Exception e) {
                    Global.ProcException(e);
                }
            } else {
                Global.Toast("格式不支持!");
                onBackPressed();
            }
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtil.d("加载进度：" + newProgress);
                if (newProgress == 100) {
                    pb_progress.setVisibility(View.GONE);
                } else {
                    pb_progress.setVisibility(View.VISIBLE);
                    pb_progress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

}
