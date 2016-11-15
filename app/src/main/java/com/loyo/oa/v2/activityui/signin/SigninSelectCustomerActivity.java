package com.loyo.oa.v2.activityui.signin;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 拜访签到【选择客户】页面
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomerActivity extends BaseActivity implements View.OnClickListener {

    //    private LinearLayout ll_back, ll_search;
    private PullToRefreshListView lv_list;
//    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_select_customer);
        initView();
    }

    private void initView() {
        findViewById(R.id.ll_back).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("选择客户");
        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
    }

    @Override
    public void onClick(View v) {
    }
}
