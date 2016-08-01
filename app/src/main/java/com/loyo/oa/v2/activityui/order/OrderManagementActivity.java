package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【订单管理】 列表 页面
 * Created by xeq on 16/8/1.
 */
public class OrderManagementActivity extends BaseActivity implements View.OnClickListener {

    private ImageView img_title_arrow;
    private LinearLayout img_title_left, ll_category;
    private RelativeLayout layout_title_action, img_title_search_right;
    private TextView tv_title_1;
    private ListView lv_order_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        init();
    }

    private void init() {
        setTitle("我的订单");
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_arrow.setVisibility(View.VISIBLE);
        lv_order_title = (ListView) findViewById(R.id.lv_sale);
        ll_category = (LinearLayout) findViewById(R.id.ll_category);
        ll_category.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        layout_title_action = (RelativeLayout) findViewById(R.id.layout_title_action);
        layout_title_action.setOnClickListener(this);
        layout_title_action.setOnTouchListener(Global.GetTouch());
        img_title_search_right = (RelativeLayout) findViewById(R.id.img_title_search_right);
        img_title_search_right.setOnClickListener(this);
        img_title_search_right.setOnTouchListener(Global.GetTouch());
        img_title_search_right.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
