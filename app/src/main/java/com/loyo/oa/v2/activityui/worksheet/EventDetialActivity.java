package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.EventDetail;
import com.loyo.oa.v2.activityui.worksheet.common.EventHandleInfoList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
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

    private LinearLayout ll_back, ll_handleInfoList;
    private TextView tv_title, tv_content, tv_responsor, tv_type, tv_worksheet, tv_status;
    private Button bt_confirm;
    private Bundle mBundle;
    private String eventId, worksheetId;
    private EventDetail mData;
    private int bundleCode = 0;
    private String typeTxt;

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
        bundleCode = intent.getIntExtra(ExtraAndResult.EXTRA_STATUS, 0);
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
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_responsor = (TextView) findViewById(R.id.tv_responsor);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_worksheet = (TextView) findViewById(R.id.tv_worksheet);
        tv_status = (TextView) findViewById(R.id.tv_status);
        ll_handleInfoList = (LinearLayout) findViewById(R.id.ll_handleInfoList);
        if (bundleCode != 0) {
            bt_confirm.setVisibility(View.VISIBLE);
            bt_confirm.setText(typeTxt);
        }
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
                mBundle.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                mBundle.putInt(ExtraAndResult.EXTRA_DATA, bundleCode /*提交完成:0x01,打回重做0x02*/);
                app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                break;
        }
    }


    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("wsId", worksheetId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getEventDetail(eventId, map, new Callback<BaseBeanT<EventDetail>>() {
                    @Override
                    public void success(BaseBeanT<EventDetail> o, Response response) {
                        HttpErrorCheck.checkResponse("事件详细：", response);
                        mData = o.data;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });

    }

    private void bindData() {
        tv_content.setText(mData.content);
        tv_responsor.setText("负责人：" + mData.responsorName);
        tv_type.setText("触发方式：" + (mData.triggerMode == 1 ? "流程触发" : "定时触发"));
        tv_worksheet.setText("所属工单：" + mData.title);
        setStatus();
        for (int i = 0; i < mData.handleInfoList.size(); i++) {
            ll_handleInfoList.addView(new EventHandleInfoList(this, mData.handleInfoList.get(i)));
        }
    }

    private void setStatus() {
        if (mData.status != 0) {
            String info = "";
            int bj = R.drawable.retange_gray;
            switch (mData.status) {
                case 1://待处理
                    info = "待处理";
                    bj = R.drawable.retange_purple;
                    break;
                case 2://未触发
                    info = "未触发";
                    bj = R.drawable.retange_gray;
                    break;
                case 3://已完成
                    info = "已完成";
                    bj = R.drawable.retange_gray;
                    break;
            }
            tv_status.setText(info);
            tv_status.setBackgroundResource(bj);
        }
    }
}
