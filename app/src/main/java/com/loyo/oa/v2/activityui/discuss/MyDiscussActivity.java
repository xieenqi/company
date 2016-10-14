package com.loyo.oa.v2.activityui.discuss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.discuss.adapter.DiscussAdapter;
import com.loyo.oa.v2.activityui.discuss.persenter.MyDisscussPControl;
import com.loyo.oa.v2.activityui.discuss.viewcontrol.MyDisscussVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的讨论】
 * create by libo 2016/3/9
 */
public class MyDiscussActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2, MyDisscussVControl {
    private PullToRefreshListView lv_discuss;
    private LinearLayout layout_back;
    private TextView tv_title;
    private TextView tv_edit;
    private DiscussAdapter adapter;

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            adapter.updataList((ArrayList<HttpDiscussItem>) msg.obj);
        }
    };

    MyDisscussPControl pControl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiscuss);
        pControl = new MyDisscussPControl(this, handler);
        initView();
        initListener();
        pControl.getPageData();
    }

    private void initView() {
        assignViews();

        /**  设置actionbar显示  **/
        tv_title.setText("我的讨论");
        tv_edit.setText("@我的");
        tv_title.setVisibility(View.VISIBLE);
        tv_edit.setVisibility(View.VISIBLE);
        lv_discuss.setMode(PullToRefreshBase.Mode.BOTH);
        lv_discuss.setOnRefreshListener(this);
        adapter = new DiscussAdapter(this);
        lv_discuss.getRefreshableView().setAdapter(adapter);
    }

    private void assignViews() {
        lv_discuss = (PullToRefreshListView) findViewById(R.id.lv_discuss);
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
                app.startActivity(this, HaitMyActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
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
    public void showProgress() {
        showLoading("");
    }

    @Override
    public void hideProgress() {
        lv_discuss.onRefreshComplete();
    }

    @Override
    public void showMsg(String message) {

    }
}
