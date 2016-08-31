package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【事件详情】页面
 * Created by xeq on 16/8/30.
 */
public class EventDetialActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_back;
    private TextView tv_title;
    private Button bt_confirm;
    private Bundle mBundle;
    private String eventId, worksheetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detial);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
        worksheetId = intent.getStringExtra(ExtraAndResult.EXTRA_ID2);
        if (TextUtils.isEmpty(worksheetId) || TextUtils.isEmpty(eventId)) {
            Toast("参数不全");
            onBackPressed();
        }
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        ll_back.setOnClickListener(this);
        bt_confirm.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("事件详情");
        getData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;

            case R.id.bt_confirm:
                mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.CC_USER_ID, "57c63f25526f1552f2000003" /*事件id*/);
                mBundle.putInt(ExtraAndResult.EXTRA_DATA, 0x10 /*提交完成:0x01,打回重做0x02*/);
                app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                break;
        }
    }


    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("wsId", worksheetId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getEventDetail(eventId, map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("事件详细：", response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });

    }

}
