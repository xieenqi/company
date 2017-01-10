package com.loyo.oa.v2.activityui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.SaleStageActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 丢公海客户 原因
 * Created by xeq on 17/1/9.
 */

public class LoseCommonCustomerReasonActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back, ll_reason;
    private TextView tv_title, tv_reason;
    private ImageView iv_submit;
    private EditText et_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose_common_customer_reason);
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        ll_reason = (LinearLayout) findViewById(R.id.ll_reason);
        et_reason = (EditText) findViewById(R.id.et_reason);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_title.setText("丢公海");
        Global.SetTouchView(ll_back, iv_submit, ll_reason);
        iv_submit.setImageResource(R.drawable.right_submit1);
        ll_back.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
        ll_reason.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_submit:
                break;
            case R.id.ll_reason:
                Bundle stage = new Bundle();
                stage.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.COMMON_REASON);
                stage.putString(ExtraAndResult.EXTRA_NAME, "丢公海原因");
                stage.putString(ExtraAndResult.EXTRA_DATA, tv_reason.getText().toString());
                app.startActivityForResult(LoseCommonCustomerReasonActivity.this, SaleStageActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                break;
        }
    }
}
