package com.loyo.oa.v2.activityui.dashboard;

import android.os.Bundle;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseLoadingActivity;

/**
 * 【仪表盘】详情页面
 * Created by xeq on 16/12/12.
 */

public class DashboardDetailActivity extends BaseLoadingActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_dashboard_detail);
    }

    @Override
    public void getPageData() {

    }

    private void initView() {
        ll_loading.setStatus(LoadingLayout.Success);
    }
}
