package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderestimate);
        initUI();
    }

    public void initUI(){

        ll_back  = (LinearLayout) findViewById(R.id.ll_back);
        ll_add  = (LinearLayout) findViewById(R.id.ll_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_totalprice = (TextView) findViewById(R.id.tv_totalprice);
        tv_aleryprice = (TextView) findViewById(R.id.tv_aleryprice);
        tv_faileprice = (TextView) findViewById(R.id.tv_faileprice);

        lv_listview = (ListView) findViewById(R.id.lv_listview);

        tv_title.setText("回款记录");

        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //新建
            case R.id.ll_add:
                Intent intent = new Intent(OrderEstimateActivity.this,OrderEstimateAddActivity.class);
                startActivity(intent);
                break;


        }
    }
}
