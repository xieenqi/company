package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.HashMap;

import static com.loyo.oa.v2.activityui.order.TerminateOrderProcessListActivity.KEY_SELECTED_PROCESS;

/**
 * Created by EthanGong on 2017/1/10.
 */

public class TerminateOrderCommitActivity extends BaseActivity {

    private final static int REQUEST_PROCESS_CODE = 100;
    public final static String KEY_ORDER_ID = "com.loyo.TerminateOrderCommitActivity.KEY_ORDER_ID";

    private LinearLayout ll_back;
    private LinearLayout ll_process;
    private TextView tv_process;
    private Button commitButton;
    private EditText editText;
    private WfTemplate selectedProcess;
    private String orderId;

    private View.OnClickListener onClickListener = new View.OnClickListener(){

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    onBackPressed();
                    break;
                case R.id.ll_process:
                {
                    Bundle mBundle = new Bundle();
                    if (selectedProcess != null) {
                        mBundle.putSerializable(KEY_SELECTED_PROCESS,
                                selectedProcess);
                    }
                    app.startActivityForResult(TerminateOrderCommitActivity.this,
                            TerminateOrderProcessListActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, REQUEST_PROCESS_CODE, mBundle);
                    break;
                }
                case R.id.btn_commit:
                {
                    // validate & commit
                    validateAndCommitData();
                }
                break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate_order_commit);
        setupUI();
        retrieveData();
    }

    private void retrieveData() {
        orderId = (String)getIntent().getSerializableExtra(KEY_ORDER_ID);
    }

    private void setupUI() {
        setTitle(R.id.tv_title, "意外终止");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(onClickListener);

        ll_process = (LinearLayout) findViewById(R.id.ll_process);
        ll_process.setOnTouchListener(Global.GetTouch());
        ll_process.setOnClickListener(onClickListener);

        tv_process = (TextView)findViewById(R.id.tv_process);

        commitButton = (Button)findViewById(R.id.btn_commit);
        commitButton.setOnTouchListener(Global.GetTouch());
        commitButton.setOnClickListener(onClickListener);

        editText = (EditText) findViewById(R.id.et_reason);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null
                && requestCode == REQUEST_PROCESS_CODE) {
            WfTemplate item = (WfTemplate)data.getSerializableExtra(
                    KEY_SELECTED_PROCESS);
            tv_process.setText(item.getTitle());
            selectedProcess = item;
        }
    }

    private void validateAndCommitData() {
        if (selectedProcess == null) {
            sweetAlertDialogView.alertIcon(null, "请选择流程");
            return;
        }
        if (TextUtils.isEmpty(editText.getText().toString())) {
            sweetAlertDialogView.alertIcon(null, "请输入终止原因");
            return;
        }

        String reason = editText.getText().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("bizFormId", selectedProcess.getBizformId());
        map.put("wfTplId", selectedProcess.getId());
        map.put("endReason", reason);
        showCommitLoading();
        OrderService.terminationOrderWithProcess(orderId, map)
                .subscribe(new DefaultLoyoSubscriber<Object>(hud) {
                    @Override
                    public void onNext(Object o) {
                        LoyoUIThread.runAfterDelay(new Runnable() {
                            @Override
                            public void run() {
                                finishWithPopAnimation();
                            }
                        },2000);
                    }
                });
    }
}
