package com.loyo.oa.v2.tool;

import android.os.Bundle;


/**
 * 继承此基类 布局必须要     <com.weavey.loading.lib.LoadingLayout/>
 * android:id="@+id/ll_loading"
 * Created by xeq on 16/12/1.
 */

public class BaseLoadingActivity extends BaseActivity {
//    private LoadingLayout ll_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ll_loading = (LoadingLayout) findViewById(R.id.ll_loading);
//        ll_loading.setStatus(LoadingLayout.Loading);
    }
}
