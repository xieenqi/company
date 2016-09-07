package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.worksheet.bean.EventDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.common.EventHandleInfoList;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventAction;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【事件详情】页面
 * Created by xeq on 16/8/30.
 */
public class EventDetialActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_back, ll_handleInfoList,layout_bottom_btn;
    private TextView tv_title, tv_content, tv_responsor, tv_type, tv_worksheet, tv_status, tv_time, tv_day;
    private Button btn_complete1,btn_complete2;
    private Bundle mBundle;
    private String eventId, worksheetId;
    private EventDetail mData;
    private WorksheetEventsSupporter worksheetEventsSupporter;
    private ArrayList<WorksheetEventAction> actions;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;

            case R.id.btn_complete1:
                mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                WorksheetEventAction aciton1 = actions.get(0);
                switch (aciton1) {
                    case Transfer:
                    case Dispatch:
                        SelectDetUserActivity2.startThisForOnly(EventDetialActivity.this, null);
                        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        break;
                    case Redo:
                    case Finish:
                        int code2 = aciton1 == WorksheetEventAction.Redo?0X01:0X02;
                        mBundle.putInt(ExtraAndResult.EXTRA_DATA,code2/*提交完成:0x01,打回重做0x02*/);
                        app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                        break;
                }
                break;

            case R.id.btn_complete2:
                mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                WorksheetEventAction aciton2 = actions.get(1);
                switch (aciton2) {
                    case Transfer:
                    case Dispatch:
                        SelectDetUserActivity2.startThisForOnly(EventDetialActivity.this, null);
                        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        break;
                    case Redo:
                    case Finish:
                        int code2 = aciton2 == WorksheetEventAction.Redo?0X01:0X02;
                        mBundle.putInt(ExtraAndResult.EXTRA_DATA,code2/*提交完成:0x01,打回重做0x02*/);
                        app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detial);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        worksheetEventsSupporter = (WorksheetEventsSupporter) intent.getSerializableExtra(ExtraAndResult.EXTRA_OBJ);
        worksheetId = intent.getStringExtra(ExtraAndResult.EXTRA_ID2);
        actions = (ArrayList<WorksheetEventAction>) intent.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        eventId = worksheetEventsSupporter.id;
        if (TextUtils.isEmpty(worksheetId) && TextUtils.isEmpty(eventId)) {
            Toast("参数不全");
            onBackPressed();
        }
    }

    private void initView() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        btn_complete1 = (Button) findViewById(R.id.btn_complete1);
        btn_complete2 = (Button) findViewById(R.id.btn_complete2);
        ll_back.setOnClickListener(this);
        btn_complete1.setOnClickListener(this);
        btn_complete2.setOnClickListener(this);
        btn_complete1.setOnTouchListener(Global.GetTouch());
        btn_complete2.setOnTouchListener(Global.GetTouch());
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("事件详情");
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_responsor = (TextView) findViewById(R.id.tv_responsor);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_worksheet = (TextView) findViewById(R.id.tv_worksheet);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_day = (TextView) findViewById(R.id.tv_day);
        ll_handleInfoList = (LinearLayout) findViewById(R.id.ll_handleInfoList);
        showLoading("");
        getData();
    }

    private void setRoleinit(){
        btn_complete1.setVisibility(View.GONE);
        btn_complete2.setVisibility(View.GONE);

        for(int i = 0;i<actions.size();i++){
            if(i == 0){
                btn_complete1.setVisibility(actions.get(i).visible() ? View.VISIBLE : View.GONE);
                btn_complete1.setText(actions.get(i).getBtnTitle());
            }else if(i == 1){
                btn_complete2.setVisibility(actions.get(i).visible() ? View.VISIBLE : View.GONE);
                btn_complete2.setText(actions.get(i).getBtnTitle());
            }
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
        tv_responsor.setText("负责人：" + (null == mData.responsorName ? "未分派" : mData.responsorName));
        tv_type.setText("触发方式：" + (mData.triggerMode == 1 ? "自动流转" : "定时触发"));
        tv_worksheet.setText("所属工单：" + mData.title);
        tv_day.setText("限时：" + (mData.daysDeadline == 0 ? "不限时" : mData.daysDeadline + "天"));
        tv_time.setText((mData.startTime == 0 ? "--" : DateTool.getDiffTime(Long.valueOf(mData.startTime + ""))) + " | " +
                (mData.endTime == 0 ? "--" : DateTool.getDiffTime(Long.valueOf(mData.endTime + "")) + "截止"));
        setStatus();
        ll_handleInfoList.removeAllViews();
        for (int i = 0; i < mData.handleInfoList.size(); i++) {
            ll_handleInfoList.addView(new EventHandleInfoList(this, mData.handleInfoList.get(i)));
        }
        setRoleinit();
    }

    private void setStatus() {
        tv_status.setText(mData.status.getName());
        tv_status.setBackgroundResource(mData.status.getStatusBackground());
    }


    /**
     * 重做回调
     * */
    @Subscribe
    public void onWorkSheetDetailsRedo(WorksheetInfo event) {
        for(int i =0 ; i <actions.size(); i++) {
            if (actions.get(i) == WorksheetEventAction.Redo) {
                actions.remove(i);
                i--;
            }
        }

        setRoleinit();
        getData();
    }

    /**
     * 提交完成 回调
     * */
    @Subscribe
    public void onWorkSheetDetailsFinish(WorksheetDetail event) {
        for(int i =0 ; i <actions.size(); i++) {
            if (actions.get(i) == WorksheetEventAction.Finish) {
                actions.remove(i);
                i--;
            }
        }

        setRoleinit();
        getData();
    }

    /**
     * 设置事件负责人
     */
    private void setEventPersonal(String userId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("responsorId", userId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                setEventPerson(eventId, map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("设置事件负责人：", response);
                        for(int i =0 ; i <actions.size(); i++) {
                            if (actions.get(i) ==  WorksheetEventAction.Dispatch) {
                                actions.remove(i);
                                actions.add(i, WorksheetEventAction.Transfer);
                            }
                        }

                        setRoleinit();
                        getData();
                        AppBus.getInstance().post(new WorksheetEventChangeEvent());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);

                    }
                });

    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
             /*用户单选, 负责人*/
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
                setEventPersonal(u.getId());
                break;
        }
    }
}
