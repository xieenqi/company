package com.loyo.oa.v2.activityui.clue;

import android.os.Bundle;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 线索 新建 页面
 * Created by xeq on 16/8/20.
 */
public class ClueAddActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getIntentData();
        initView();
    }

    private void getIntentData() {
//        orderId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
//        if (TextUtils.isEmpty(orderId)) {
//            onBackPressed();
//            Toast("参数不全");
//        }
    }

    private void initView() {
//        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
//        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
//        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
//        img_title_left.setOnTouchListener(Global.GetTouch());
//        img_title_right.setOnTouchListener(Global.GetTouch());
//        tv_title_1.setText("订单详情");
//        img_title_left.setOnClickListener(this);
//        img_title_right.setOnClickListener(this);
    }
}
