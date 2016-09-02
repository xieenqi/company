package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.activityui.worksheet.common.WSRole;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetCommon;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventAction;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventCell;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventLayout;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetPermisssion;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.squareup.otto.Subscribe;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【 工单详情 】  页面
 * Created by xeq on 16/8/27.
 */
public class WorksheetDetailActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout img_title_left;
    private LinearLayout ll_worksheet_info;
    private LinearLayout ll_events;
    private TextView tv_title_1, tv_title, tv_status, tv_assignment, tv_complete_number, tv_setting;
    private RelativeLayout img_title_right;
    private String worksheetId, eventId;
    private BaseBeanT<WorksheetDetail> mData;
    private WorksheetDetail detail;
    private boolean isAssignment, isCreated;//分派人 ，创建人

    //处理事件
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case ExtraAndResult.REQUEST_CODE_CUSTOMER://到事件详情
                    Bundle bundle = new Bundle();
                    bundle.putString(ExtraAndResult.EXTRA_ID, (String) msg.obj);
                    bundle.putString(ExtraAndResult.EXTRA_ID2, mData.data.id);
                    bundle.putInt(ExtraAndResult.EXTRA_STATUS, msg.arg1 == 2 ? 0x02 : 0x10);
                    app.startActivityForResult(WorksheetDetailActivity.this, EventDetialActivity.class, MainApp.ENTER_TYPE_RIGHT, 1, bundle);
                    break;
                case ExtraAndResult.REQUEST_CODE_STAGE://设置负责人
                    eventId = (String) msg.obj;
                    SelectDetUserActivity2.startThisForOnly(WorksheetDetailActivity.this, null);
                    overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    break;
                case ExtraAndResult.REQUEST_CODE_PRODUCT://事件重做
                    Bundle mBundle = new Bundle();
                    mBundle.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                    mBundle.putInt(ExtraAndResult.EXTRA_DATA, 0x02 /*提交完成:0x01,打回重做0x02*/);
                    app.startActivity(WorksheetDetailActivity.this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                    break;
                case ExtraAndResult.REQUEST_CODE_TYPE://事件提交完成
                    Bundle bd = new Bundle();
                    bd.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                    bd.putInt(ExtraAndResult.EXTRA_DATA, 0x10 /*提交完成:0x01,打回重做0x02*/);
                    app.startActivity(WorksheetDetailActivity.this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bd);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_detial);
        getIntentData();
        initView();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        worksheetId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
        if (TextUtils.isEmpty(worksheetId)) {
            Toast("参数不全");
            onBackPressed();
        }
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setVisibility(View.INVISIBLE);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        ll_worksheet_info = (LinearLayout) findViewById(R.id.ll_worksheet_info);
        ll_events = (LinearLayout) findViewById(R.id.ll_events);
        ll_worksheet_info.setOnClickListener(this);
        tv_title_1.setText("工单详情");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_assignment = (TextView) findViewById(R.id.tv_assignment);
        tv_complete_number = (TextView) findViewById(R.id.tv_complete_number);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_setting.setOnClickListener(this);
        getData();
    }

    private void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getWorksheetDetail(worksheetId, new Callback<BaseBeanT<WorksheetDetail>>() {
                    @Override
                    public void success(BaseBeanT<WorksheetDetail> result, Response response) {
                        HttpErrorCheck.checkResponse("工单详情：", response);
                        if (result.errcode == 0) {
                            mData = result;
                            detail = mData.data;
                            loadData();
                        }
                        else {
                            Toast(""+mData.errmsg);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_right:
                functionButton();
                break;
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_worksheet_info:
                Bundle bundle = new Bundle();
                bundle.putSerializable(ExtraAndResult.CC_USER_ID, mData.data.id);
                app.startActivityForResult(this, WorksheetInfoActivity.class, 0, this.RESULT_FIRST_USER, bundle);
                break;
            case R.id.tv_setting://批量设置
                eventId = "";
                SelectDetUserActivity2.startThisForOnly(WorksheetDetailActivity.this, null);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.bt_confirm://提交完成
                break;
        }
    }

    private void loadData() {
        if (MainApp.user.id.equals(mData.data.dispatcher.getId())) {
            isAssignment = true;
            img_title_right.setVisibility(View.VISIBLE);
        }
        if (MainApp.user.id.equals(mData.data.creator.getId())) {
            isCreated = true;
            img_title_right.setVisibility(View.INVISIBLE);
            tv_setting.setVisibility(View.INVISIBLE);
        }
        if (ll_events.getChildCount() > 0) {
            ll_events.removeAllViews();
        }
        tv_title.setText(detail.title);
        tv_assignment.setText("分派人：" + detail.dispatcher.getName());
        tv_status.setText(detail.status.getName());
        tv_status.setBackgroundColor(detail.status.getColor());

        if (null == detail.sheetEventsSupporter) {
            return;
        }

        for (int i = 0; i < detail.sheetEventsSupporter.size(); i++) {

            WorksheetEventsSupporter event = detail.sheetEventsSupporter.get(i);
            boolean isResponsor = MainApp.user.id.equals(event.responsorId);

            /* 同一人可能同时有多个角色，也就有多个操作 */
            WSRole role = new WSRole();
            if (isAssignment) {
                role.addRole(WSRole.Dispatcher);
            }
            if (isCreated) {
                role.addRole(WSRole.Creator);
            }
            if (isResponsor) {
                role.addRole(WSRole.Responsor);
            }

            WorksheetEventCell cell = new WorksheetEventCell(this, handler);
            cell.loadData(event, role, actionsForRole(event, role));

            ll_events.addView(cell);
        }
    }

    public ArrayList<WorksheetEventAction> actionsForRole(WorksheetEventsSupporter event, WSRole role) {
        WorksheetStatus status = detail.status;
        WorksheetEventStatus eventStatus = event.status;
        boolean hasResponsor = (event.responsorId != null);
        return WorksheetPermisssion.actionsFor(status, role, eventStatus, hasResponsor);
    }

    /**
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(WorksheetDetailActivity.this).builder();
        dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                stopWorksheet();
            }
        });
        dialog.show();
    }

    /**
     * 终止 工单
     */
    private void stopWorksheet() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", 5);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                setStpoWorksheet(worksheetId, map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("意外终止工单：", response);
                        Toast("操作成功");
                        onBackPressed();
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
                if (!TextUtils.isEmpty(eventId)) {
                    setEventPersonal(u.getId());
                } else {
                    setAllEventPersonal(u.getId());
                }
                break;
        }
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
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);

                    }
                });

    }

    /**
     * 设置事件负责人
     */
    private void setAllEventPersonal(String userId) {
        List<String> eventIsList = new ArrayList<>();
        for (int i = 0; i < mData.data.sheetEventsSupporter.size(); i++) {
            eventIsList.add(mData.data.sheetEventsSupporter.get(i).id);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("responsorId", userId);
        map.put("eventIds", eventIsList);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                setAllEventPerson(map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("设置all事件负责人：", response);
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);

                    }
                });
    }

    /**
     * 调用此方法  批量设置按钮 显现出来
     */
    public void setSetting() {
        tv_setting.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onWorkSheetDetailsRush(WorksheetDetail event) {
        Toast("回调刷新");
    }
}
