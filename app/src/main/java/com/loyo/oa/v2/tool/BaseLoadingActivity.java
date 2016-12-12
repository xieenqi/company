package com.loyo.oa.v2.tool;

import android.os.Bundle;
import android.view.View;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;


/**
 * 继承此基类 布局必须要     <com.weavey.loading.lib.LoadingLayout/>
 * android:id="@+id/ll_loading"
 * Created by xeq on 16/12/1.(不是框架做的页面有效)
 */

public abstract class BaseLoadingActivity extends BaseActivity {
    public LoadingLayout ll_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutView();
        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getPageData();
            }
        });
    }

    public abstract void setLayoutView();

    public abstract void getPageData();

}
