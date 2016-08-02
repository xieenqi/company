package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【订单详情】页面
 * Created by xeq on 16/8/2.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout img_title_left;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, tv_title, tv_status, tv_customer, tv_money, tv_product, tv_plan, tv_plan_value,
            tv_record, tv_record_value, tv_enclosure, tv_enclosure_value, tv_responsible_name, tv_creator_name, tv_creator_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("订单详情");
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        tv_plan_value = (TextView) findViewById(R.id.tv_plan_value);
        tv_record = (TextView) findViewById(R.id.tv_record);
        tv_record_value = (TextView) findViewById(R.id.tv_record_value);
        tv_enclosure = (TextView) findViewById(R.id.tv_enclosure);
        tv_enclosure_value = (TextView) findViewById(R.id.tv_enclosure_value);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_creator_name = (TextView) findViewById(R.id.tv_creator_name);
        tv_creator_time = (TextView) findViewById(R.id.tv_creator_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                break;
        }

    }
}
