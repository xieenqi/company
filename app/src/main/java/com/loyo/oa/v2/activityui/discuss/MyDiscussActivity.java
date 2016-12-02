package com.loyo.oa.v2.activityui.discuss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.adapter.DiscussAdapter2;
import com.loyo.oa.v2.activityui.discuss.persenter.MyDisscussPControl;
import com.loyo.oa.v2.activityui.discuss.viewcontrol.MyDisscussVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【我的讨论】
 * create by libo 2016/3/9
 */
public class MyDiscussActivity extends BaseLoadingActivity implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2, MyDisscussVControl {
    private PullToRefreshRecyclerView2 lv_discuss;
    private LinearLayout layout_back;
    private TextView tv_title;
    private TextView tv_edit;
    private DiscussAdapter2 adapter;

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            adapter.updataList((ArrayList<HttpDiscussItem>) msg.obj);
            ll_loading.setStatus(LoadingLayout.Success);
        }
    };

    MyDisscussPControl pControl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pControl = new MyDisscussPControl(this, handler);
        initView();
        initListener();
        getPageData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_mydiscuss);
    }

    @Override
    public void getPageData() {
        pControl.getPageData();
    }

    private void initView() {
        assignViews();

        /**  设置actionbar显示  **/
        tv_title.setText("我的讨论");
        tv_edit.setText("@我的");
        tv_title.setVisibility(View.VISIBLE);
        tv_edit.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_discuss.getRefreshableView().setLayoutManager(layoutManager);
        lv_discuss.setMode(PullToRefreshBase.Mode.BOTH);
        lv_discuss.setOnRefreshListener(this);
        adapter = new DiscussAdapter2(this);
        lv_discuss.getRefreshableView().setAdapter(adapter);
    }

    private void assignViews() {
        lv_discuss = (PullToRefreshRecyclerView2) findViewById(R.id.lv_discuss);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
    }

    private void initListener() {
        layout_back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        lv_discuss.setOnRefreshListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.tv_edit:
                app.startActivityForResult(this, HaitMyActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE, null);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        pControl.onPullDown();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        pControl.onPullUp();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE:
                    pControl.onPullDown();
                    LogUtil.d("数组刷新了红点数据");
                    break;
            }
        }
    }


    @Override
    public void showProgress(String msg) {
        showLoading(msg);
    }

    @Override
    public void hideProgress() {
        lv_discuss.onRefreshComplete();
    }

    @Override
    public void showMsg(String message) {

    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }
}
