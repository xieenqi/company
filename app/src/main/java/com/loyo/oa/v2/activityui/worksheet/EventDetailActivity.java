package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.EventDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.common.EventHandleInfoList;
import com.loyo.oa.v2.activityui.worksheet.common.WSRole;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventAction;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetPermisssion;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【事件详情】页面
 * Created by xeq on 16/8/30.
 */
public class EventDetailActivity extends BaseLoadingActivity implements View.OnClickListener {

    private static String PICK_USER_SESSION = "com.loyo.EventDetailActivity.PICK_USER_SESSION";

    private LinearLayout ll_back, ll_handleInfoList, layout_bottom_btn;
    private TextView tv_title, tv_content, tv_responsor, tv_type, tv_worksheet, tv_status, tv_startTime, tv_endTime, tv_day;
    private Button btn_complete1, btn_complete2;
    private Bundle mBundle;
    private String eventId, worksheetId;
    private EventDetail mData;
    private WorksheetDetail worksheetDetail;
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
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
                        bundle.putSerializable(ContactPickerActivity.SESSION_KEY, EventDetailActivity.PICK_USER_SESSION);
                        Intent intent = new Intent();
                        intent.setClass(this, ContactPickerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case Redo:
                    case Finish:
                        int code2 = aciton1 == WorksheetEventAction.Redo ? 0X01 : 0X02;
                        mBundle.putInt(ExtraAndResult.EXTRA_DATA, code2/*提交完成:0x01,打回重做0x02*/);
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
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                        bundle.putSerializable(ContactPickerActivity.SESSION_KEY, EventDetailActivity.PICK_USER_SESSION);
                        bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
                        Intent intent = new Intent();
                        intent.setClass(this, ContactPickerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case Redo:
                    case Finish:
                        int code2 = aciton2 == WorksheetEventAction.Redo ? 0X01 : 0X02;
                        mBundle.putInt(ExtraAndResult.EXTRA_DATA, code2/*提交完成:0x01,打回重做0x02*/);
                        app.startActivity(this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_event_detial_new);
    }

    @Override
    public void getPageData() {
        getData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        worksheetEventsSupporter = (WorksheetEventsSupporter) intent.getSerializableExtra(ExtraAndResult.EXTRA_OBJ);
        worksheetId = intent.getStringExtra(ExtraAndResult.EXTRA_ID2);
        worksheetDetail = (WorksheetDetail) intent.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        eventId = worksheetEventsSupporter.id;
        actions = new ArrayList<WorksheetEventAction>();
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
        tv_startTime = (TextView) findViewById(R.id.tv_startTime);
        tv_endTime = (TextView) findViewById(R.id.tv_endTime);
        tv_day = (TextView) findViewById(R.id.tv_day);
        ll_handleInfoList = (LinearLayout) findViewById(R.id.ll_handleInfoList);
        getPageData();
    }

    private void setRoleinit() {
        btn_complete1.setVisibility(View.GONE);
        btn_complete2.setVisibility(View.GONE);

        for (int i = 0; i < actions.size(); i++) {
            if (i == 0) {
                btn_complete1.setVisibility(actions.get(i).visible() ? View.VISIBLE : View.GONE);
                btn_complete1.setText(actions.get(i).getBtnTitle());
            } else if (i == 1) {
                btn_complete2.setVisibility(actions.get(i).visible() ? View.VISIBLE : View.GONE);
                btn_complete2.setText(actions.get(i).getBtnTitle());
            }
        }
    }


    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("wsId", worksheetId);
        WorksheetService.getEventDetail(eventId, map)
                .subscribe(new DefaultLoyoSubscriber<EventDetail>(ll_loading) {
                    @Override
                    public void onNext(EventDetail detail) {
                        mData = detail;
                        actions = actionsForRole(mData, getRoleForEvent(mData));
                        bindData();
                    }
                });

    }

    private void bindData() {
        ll_loading.setStatus(LoadingLayout.Success);
        tv_content.setText(mData.content);
        tv_responsor.setText("负责人：" + (null == mData.responsorName ? "未分派" : mData.responsorName));
        tv_type.setText("触发方式：" + (mData.triggerMode == 1 ? "自动流转" : "定时触发"));
        tv_worksheet.setText("所属工单：" + mData.title);
        tv_day.setText("限时：" + (mData.daysDeadline == 0 ? "不限时" : mData.daysDeadline + "天"));
        String endTimeText = (mData.endTime == 0 ? "--" : DateTool.getDateTimeFriendly(Long.valueOf(mData.endTime + "")) + "截止");
        tv_startTime.setText((mData.startTime == 0 ? "--" : DateTool.getDateTimeFriendly(Long.valueOf(mData.startTime + ""))) + " | ");
        if (mData.isOvertime) {
            tv_endTime.setTextColor(getResources().getColor(R.color.red1));
            tv_endTime.setText(endTimeText + "(超时)");
        } else {
            tv_endTime.setText(endTimeText);
        }
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

    public WSRole getRoleForEvent(EventDetail event) {

        /* 同一人可能同时有多个角色，也就有多个操作 */
        WSRole role = new WSRole();
        if (MainApp.user.id.equals(worksheetDetail.dispatcher.getId())) {
            role.addRole(WSRole.Dispatcher);
        }
        if (MainApp.user.id.equals(worksheetDetail.creator.getId())) {
            role.addRole(WSRole.Creator);
        }
        if (MainApp.user.id.equals(event.responsorId)) {
            role.addRole(WSRole.Responsor);
        }
        return role;
    }

    public ArrayList<WorksheetEventAction> actionsForRole(EventDetail event, WSRole role) {
        WorksheetStatus status = worksheetDetail.status;
        WorksheetEventStatus eventStatus = event.status;
        boolean hasResponsor = (event.responsorId != null);
        return WorksheetPermisssion.actionsFor(status, role, eventStatus, hasResponsor);
    }


    /**
     * 重做回调
     */
    @Subscribe
    public void onWorkSheetDetailsRedo(WorksheetInfo event) {
        for (int i = 0; i < actions.size(); i++) {
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
     */
    @Subscribe
    public void onWorkSheetDetailsFinish(WorksheetDetail event) {
        for (int i = 0; i < actions.size(); i++) {
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
    private void setEventPersonal(final String userId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("responsorId", userId);
        WorksheetService.setEventPerson(eventId, map)
                .subscribe(new DefaultLoyoSubscriber<Object>() {

                    @Override
                    public void onNext(Object o) {
                        for (int i = 0; i < actions.size(); i++) {
                            if (actions.get(i) == WorksheetEventAction.Dispatch) {
                                actions.remove(i);
                                actions.add(i, WorksheetEventAction.Transfer);
                            }
                        }

                        mData.responsorId = userId;
                        actions = actionsForRole(mData, getRoleForEvent(mData));

                        setRoleinit();
                        getData();
                        AppBus.getInstance().post(new WorksheetEventChangeEvent());
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
            case FinalVariables.REQUEST_ONLY:
                OrganizationalMember u = (OrganizationalMember) data.getSerializableExtra("data");
                setEventPersonal(u.getId());
                break;
        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)
                &&
                EventDetailActivity.PICK_USER_SESSION.equals(event.session)) {
            StaffMemberCollection collection = event.data;
            OrganizationalMember newUser = Compat.convertStaffCollectionToNewUser(collection);
            if (newUser != null) {
                setEventPersonal(newUser.getId());
            }
        }
    }
}
