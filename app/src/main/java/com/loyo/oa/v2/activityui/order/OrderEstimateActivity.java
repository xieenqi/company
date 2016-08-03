package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
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
public class OrderEstimateActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private TextView     tv_title;
    private TextView     tv_dealprice;  //成交金额
    private TextView     tv_totalprice; //交易总金额
    private TextView     tv_aleryprice; //回款金额
    private TextView     tv_faileprice; //未回款

    private ListView lv_listview;
    private EstimateAdd  mEstimateAdd;
    private ArrayList<EstimateAdd> mData;
    private OrderEstimateListAdapter mAdapter;
    private Intent mIntent;
    private String dealPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderestimate);
        initUI();
    }

    public void initUI(){
        mIntent = getIntent();
        if(null != mIntent){
            dealPrice = mIntent.getStringExtra("price");
            mData     = (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data");
        }

        ll_back  = (LinearLayout) findViewById(R.id.ll_back);
        ll_add  = (LinearLayout) findViewById(R.id.ll_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_totalprice = (TextView) findViewById(R.id.tv_totalprice);
        tv_aleryprice = (TextView) findViewById(R.id.tv_aleryprice);
        tv_faileprice = (TextView) findViewById(R.id.tv_faileprice);
        tv_dealprice  = (TextView) findViewById(R.id.tv_dealprice);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        tv_title.setText("回款记录");
        if(null != dealPrice)
        tv_dealprice.setText("￥"+dealPrice);

        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        adapterInit();
    }

    public void adapterInit(){
        if(null == mAdapter){
            mData = new ArrayList<EstimateAdd>();
            mAdapter = new OrderEstimateListAdapter(this,mData);
            lv_listview.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //后退
            case R.id.ll_back:
                if(mData.size() != 0){
                    mIntent = new Intent();
                    mIntent.putExtra("data",mData);
                    app.finishActivity(this,MainApp.ENTER_TYPE_LEFT,RESULT_OK,mIntent);
                }else{
                    onBackPressed();
                }
                break;

            //新建
            case R.id.ll_add:
                app.startActivityForResult(this,OrderEstimateAddActivity.class,MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE,null);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK && null == data){
            return;
        }

        if(requestCode == ExtraAndResult.REQUEST_CODE_STAGE){
            mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
            mData.add(mEstimateAdd);
            adapterInit();
        }
    }
}
