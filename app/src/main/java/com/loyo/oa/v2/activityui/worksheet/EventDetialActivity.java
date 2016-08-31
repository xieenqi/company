package com.loyo.oa.v2.activityui.worksheet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【事件详情】页面
 * Created by xeq on 16/8/30.
 */
public class EventDetialActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private TextView tv_title;
    private Button bt_confirm;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detial);
        initView();
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        ll_back.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("事件详情");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;

            case R.id.bt_confirm:
                mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.CC_USER_ID,"57c63f25526f1552f2000003" /*事件id*/);
                mBundle.putInt(ExtraAndResult.EXTRA_DATA,0x10 /*提交完成:0x01,打回重做0x02*/);
                app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT,false,mBundle);
                break;
        }

    }
}
