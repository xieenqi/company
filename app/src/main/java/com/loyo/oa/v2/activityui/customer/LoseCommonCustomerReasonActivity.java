package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.sale.SaleStageActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.HashMap;

/**
 * 丢公海客户 原因
 * Created by xeq on 17/1/9.
 */

public class LoseCommonCustomerReasonActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back, ll_reason;
    private TextView tv_title, tv_reason;
    private ImageView iv_submit;
    private EditText et_reason;
    private SaleStage stage;
    private boolean isReason = false;
    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose_common_customer_reason);
        customerId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        ll_reason = (LinearLayout) findViewById(R.id.ll_reason);
        et_reason = (EditText) findViewById(R.id.et_reason);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_title.setText("投入公海");
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
                if (stage == null) {
                    Toast("请选择丢公海原因");
                    return;
                }
                if (isReason && TextUtils.isEmpty(et_reason.getText().toString())) {
                    Toast("请输入原因说明");
                    return;
                }
                showCommitLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", stage.id);
                map.put("comment", et_reason.getText().toString());
                CustomerService.dumpCustomer(customerId, map)
                        .subscribe(new DefaultLoyoSubscriber<BaseResponse<Object>>(hud) {
                            @Override
                            public void onNext(BaseResponse<Object> customer) {
                                setResult(RESULT_OK);
                                onBackPressed();
                            }
                        });
                break;
            case R.id.ll_reason:
                isReason = false;
                Bundle stage = new Bundle();
                stage.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.COMMON_REASON);
                stage.putString(ExtraAndResult.EXTRA_NAME, "丢公海原因");
                stage.putString(ExtraAndResult.EXTRA_DATA, tv_reason.getText().toString());
                app.startActivityForResult(LoseCommonCustomerReasonActivity.this, SaleStageActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case ExtraAndResult.REQUEST_CODE_STAGE:
                stage = (SaleStage) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                if (stage != null) {
                    tv_reason.setText(stage.name);
                    if (stage.isSys && stage.name.startsWith("其他")) {
                        isReason = true;
                        et_reason.setHint("必填，请输入原因说明");
                    }
                }
                break;
        }
    }
}
