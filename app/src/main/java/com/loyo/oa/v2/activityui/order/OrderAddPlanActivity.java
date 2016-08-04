package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 添加 回款计划
 * Created by xeq on 16/8/4.
 */
public class OrderAddPlanActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_title, tv_time, tv_tx, tv_kind;
    private LinearLayout ll_back, ll_time, ll_tx, ll_kind;
    private ImageView iv_submit;
    private EditText et_remake, et_estprice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initUI();
    }

    public void initUI() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("新增回款计划");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(this);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        iv_submit.setOnClickListener(this);
        iv_submit.setOnTouchListener(Global.GetTouch());
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_time.setOnClickListener(this);
        ll_time.setOnTouchListener(Global.GetTouch());
        ll_tx = (LinearLayout) findViewById(R.id.ll_tx);
        ll_tx.setOnClickListener(this);
        ll_tx.setOnTouchListener(Global.GetTouch());
        ll_kind = (LinearLayout) findViewById(R.id.ll_kind);
        ll_kind.setOnClickListener(this);
        ll_kind.setOnTouchListener(Global.GetTouch());
        et_remake = (EditText) findViewById(R.id.et_remake);
        tv_time = (TextView) findViewById(R.id.tv_time);
        et_estprice = (EditText) findViewById(R.id.et_estprice);
        tv_tx = (TextView) findViewById(R.id.tv_tx);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_submit:
                break;
            case R.id.ll_time:
                break;
            case R.id.ll_tx:
                break;
            case R.id.ll_kind:
                break;
        }
    }
}
