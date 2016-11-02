package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loyo.oa.v2.application.MainApp;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by xeq on 16/10/31.
 */

public class CommonWebView extends WebView {
    public CommonWebView(Context context, String url) {
        super(context);
        initView(url);
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(String url) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(MainApp.getMainApp(), "imageloader/Cache");
        this.setInitialScale(39);// 为25%，最小缩放等级
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings setting = this.getSettings();
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultFixedFontSize(200);
        setting.setMinimumFontSize(200);
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);//重要
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 自适应屏幕宽
        setting.setAllowContentAccess(true);
        setting.setAppCacheEnabled(true);//缓存可用
        setting.setAppCachePath(cacheDir.getPath());//缓存路径
        setting.setDefaultTextEncodingName("UTF-8");
        setting.setTextZoom(200);
        this.loadDataWithBaseURL("about:blank", url, "text/html",
                "utf-8", null);
    }
}
