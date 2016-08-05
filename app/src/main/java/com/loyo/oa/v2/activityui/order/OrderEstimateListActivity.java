package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.OrderEstimateListAdapter;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【订单回款】
 * Created by yyy on 16/8/2.
 */
public class OrderEstimateListActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private TextView tv_title;
    private TextView tv_dealprice;  //成交金额
    private TextView tv_totalprice; //交易总金额
    private TextView tv_aleryprice; //回款金额
    private TextView tv_faileprice; //未回款

    private ListView lv_listview;
    private EstimateAdd mEstimateAdd;
    private ArrayList<EstimateAdd> mData;
    private OrderEstimateListAdapter mAdapter;
    private Intent mIntent;
    private Bundle mBundle;
    private String dealPrice;
    private int    fromPage;

    /**
     * 来自新建订单
     * */
    public static int PAGE_ADD = 0x01;

    /**
     * 来自编辑订单
     * */
    public static int PAGE_EDIT = 0x02;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == ExtraAndResult.MSG_WHAT_DIALOG) {
                ll_add.setVisibility(View.GONE);
            } else if (msg.what == ExtraAndResult.MSG_WHAT_GONG) {
                mBundle = msg.getData();
                mData.remove(mBundle.getInt("posi"));
                adapterInit();
                ll_add.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderestimate);
        initUI();
    }

    public void initUI() {
        mIntent = getIntent();
        if (null != mIntent) {
            fromPage  = mIntent.getIntExtra("fromPage",PAGE_ADD);
            dealPrice = mIntent.getStringExtra("price");
            mData = (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data");

        }

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_totalprice = (TextView) findViewById(R.id.tv_totalprice);
        tv_aleryprice = (TextView) findViewById(R.id.tv_aleryprice);
        tv_faileprice = (TextView) findViewById(R.id.tv_faileprice);
        tv_dealprice = (TextView) findViewById(R.id.tv_dealprice);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        tv_title.setText("回款记录");
        if (null != dealPrice)
            tv_dealprice.setText("￥" + dealPrice);
        if (null != mData)
            ll_add.setVisibility(View.GONE);
        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        adapterInit();
    }

    public void adapterInit() {
        if (null == mAdapter) {
            if (null == mData){
                mData = new ArrayList<EstimateAdd>();
            }
            mAdapter = new OrderEstimateListAdapter(this, mData, mHandler);
            lv_listview.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //新建
            case R.id.ll_add:
                app.startActivityForResult(this, OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, null);
                break;

        }
    }


    @Override
    public void onBackPressed() {
        if (mData.size() != 0) {
            mIntent = new Intent();
            mIntent.putExtra("data", mData);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && null == data) {
            return;
        }

        if (requestCode == ExtraAndResult.REQUEST_CODE_STAGE) {
            mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
            LogUtil.dee("mEstimateAdd:"+MainApp.gson.toJson(mEstimateAdd));
            if(fromPage == PAGE_ADD){
                mData.clear();
            }
            mData.add(mEstimateAdd);
            adapterInit();
            mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_DIALOG);
        }
    }
}
