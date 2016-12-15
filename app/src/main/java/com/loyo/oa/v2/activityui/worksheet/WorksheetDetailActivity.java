package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventsSupporter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.common.WSRole;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventAction;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventCell;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetPermisssion;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventChangeEvent;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【 工单详情 】  页面
 * Created by xeq on 16/8/27.
 */
public class WorksheetDetailActivity extends BaseLoadingActivity implements View.OnClickListener {
    private static String PICK_USER_SESSION = "com.loyo.WorksheetDetailActivity.PICK_USER_SESSION";
    private LinearLayout img_title_left;
    private LinearLayout ll_worksheet_info;
    private LinearLayout ll_events, ll_wran;
    private Button bt_confirm;
    private TextView tv_title_1, tv_title, tv_status, tv_assignment, tv_complete_number, tv_setting;
    private RelativeLayout img_title_right;
    private String worksheetId, eventId;
    private WorksheetDetail detail;

    //处理事件
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {

                case ExtraAndResult.WORKSHEET_EVENT_DETAIL://到事件详情
                {
                    Bundle bundle = new Bundle();
                    WSRole role = getRoleforEvent((WorksheetEventsSupporter) msg.obj);
                    ArrayList<WorksheetEventAction> actions = actionsForRole((WorksheetEventsSupporter) msg.obj, role);
                    bundle.putSerializable(ExtraAndResult.EXTRA_OBJ, (WorksheetEventsSupporter) msg.obj);
                    bundle.putSerializable(ExtraAndResult.EXTRA_DATA, (WorksheetDetail) detail);
                    bundle.putString(ExtraAndResult.EXTRA_ID2, detail.id);
                    app.startActivityForResult(WorksheetDetailActivity.this, EventDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, 1, bundle);

                }
                break;
                case ExtraAndResult.WORKSHEET_EVENT_TRANSFER://设置负责人
                case ExtraAndResult.WORKSHEET_EVENT_DISPATCH://设置负责人
                    eventId = (String) msg.obj;
                {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                    bundle.putSerializable(ContactPickerActivity.SESSION_KEY, WorksheetDetailActivity.PICK_USER_SESSION);
                    Intent intent = new Intent();
                    intent.setClass(WorksheetDetailActivity.this, ContactPickerActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
                case ExtraAndResult.WORKSHEET_EVENT_REDO://事件重做
                {
                    eventId = (String) msg.obj;
                    Bundle mBundle = new Bundle();
                    mBundle.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                    mBundle.putInt(ExtraAndResult.EXTRA_DATA, 0x01 /*提交完成:0x02,打回重做0x01*/);
                    app.startActivity(WorksheetDetailActivity.this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                }
                break;
                case ExtraAndResult.WORKSHEET_EVENT_FINISH://事件提交完成
                {
                    eventId = (String) msg.obj;
                    Bundle bd = new Bundle();
                    bd.putString(ExtraAndResult.CC_USER_ID, eventId /*事件id*/);
                    bd.putInt(ExtraAndResult.EXTRA_DATA, 0x02 /*提交完成:0x02,打回重做0x01*/);
                    app.startActivity(WorksheetDetailActivity.this, WorksheetSubmitActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bd);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_worksheet_detial);
    }

    @Override
    public void getPageData() {
        getData();
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
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);
        bt_confirm.setOnTouchListener(Global.GetTouch());
        ll_wran = (LinearLayout) findViewById(R.id.ll_wran);
        getPageData();
    }

    private void getData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getWorksheetDetail(worksheetId, new Callback<BaseBeanT<WorksheetDetail>>() {
                    @Override
                    public void success(BaseBeanT<WorksheetDetail> result, Response response) {
                        HttpErrorCheck.checkResponse("工单详情：", response,ll_loading);
                        if (result.errcode == 0) {
                            detail = result.data;
                            loadData();
                        }
//                        else {
//                            Toast("" + result.errmsg);
//                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
//                        onBackPressed();
                        HttpErrorCheck.checkError(error,ll_loading);
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
            case R.id.ll_worksheet_info://进入事件信息
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ExtraAndResult.CC_USER_ID, detail.id);
                app.startActivityForResult(this, WorksheetInfoActivity.class, 0, this.RESULT_FIRST_USER, bundle);
            }
            break;
            case R.id.tv_setting://批量设置
                eventId = "";
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                bundle.putSerializable(ContactPickerActivity.SESSION_KEY, WorksheetDetailActivity.PICK_USER_SESSION);
                Intent intent = new Intent();
                intent.setClass(WorksheetDetailActivity.this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
            case R.id.bt_confirm://提交完成
                stopWorksheet(4);
                break;
        }
    }

    private void loadData() {
        tv_setting.setVisibility(View.INVISIBLE);
        img_title_right.setVisibility(View.INVISIBLE);
        bt_confirm.setVisibility(View.INVISIBLE);
        if (MainApp.user.id.equals(detail.dispatcher.getId())) {
            if (detail.status != WorksheetStatus.TEMINATED) {
                img_title_right.setVisibility(View.VISIBLE);
            }
            if (detail.status == WorksheetStatus.WAITASSIGN) {
                ll_wran.setVisibility(View.VISIBLE);
            } else if (detail.status != WorksheetStatus.WAITASSIGN) {
                ll_wran.setVisibility(View.GONE);
            }
            if (detail.status == WorksheetStatus.WAITAPPROVE) {
                bt_confirm.setVisibility(View.VISIBLE);
            }
            if (detail.status == WorksheetStatus.WAITASSIGN) {
                tv_setting.setVisibility(View.VISIBLE);
            }
        }

        if (ll_events.getChildCount() > 0) {
            ll_events.removeAllViews();
        }
        tv_title.setText(detail.title);
        tv_assignment.setText("分派人：" + detail.dispatcher.getName());
        tv_status.setText(detail.status.getName());
        tv_status.setBackgroundResource(detail.status.getStatusBackground());

        tv_complete_number.setText("  ( " + detail.getFinshedNum() + "/" + detail.getTotalNum() + " )");

        ll_events.removeAllViews();

        if (null == detail.sheetEventsSupporter) {
            return;
        }
        for (int i = 0; i < detail.sheetEventsSupporter.size(); i++) {

            WorksheetEventsSupporter event = detail.sheetEventsSupporter.get(i);
            WSRole role = getRoleforEvent(event);

            getRoleforEvent(event);

            WorksheetEventCell cell = new WorksheetEventCell(this, handler);
            cell.loadData(event, role, actionsForRole(event, role), detail.status);

            ll_events.addView(cell);
            LogUtil.dee("执行");
        }
    }

    public WSRole getRoleforEvent(WorksheetEventsSupporter event) {

        /* 同一人可能同时有多个角色，也就有多个操作 */
        WSRole role = new WSRole();
        if (MainApp.user.id.equals(detail.dispatcher.getId())) {
            role.addRole(WSRole.Dispatcher);
        }
        if (MainApp.user.id.equals(detail.creator.getId())) {
            role.addRole(WSRole.Creator);
        }
        if (MainApp.user.id.equals(event.responsorId)) {
            role.addRole(WSRole.Responsor);
        }
        return role;
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
        dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {

                sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        stopWorksheet(5);
                        dismissSweetAlert();
                    }
                }, "提示", "意外终止后不可恢复，此工单将无法进行任何操作。\n" +
                        "您确定要终止吗？");

/*                final GeneralPopView warn = showGeneralDialog(true, true, "意外终止后不可恢复，此工单将无法进行任何操作。\n" +
                        "您确定要终止吗？");
                warn.setSureOnclick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopWorksheet(5);
                        warn.dismisDialog();
                    }
                });
                warn.setNoCancelOnclick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        warn.dismisDialog();
                    }
                });*/
            }
        });
        dialog.show();
    }

    /**
     * 终止 工单  修改工单状态
     *
     * @param status 5 意外终止  4 已完成
     */
    private void stopWorksheet(int status) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", status);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                setStpoWorksheet(worksheetId, map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("意外终止工单：", response);
                        showLoading("");
                        getData();
                        Toast("操作成功");
                        bt_confirm.setVisibility(View.GONE);
//                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        if (WorksheetDetailActivity.PICK_USER_SESSION.equals(event.session)) {
            StaffMemberCollection collection = event.data;
            NewUser u = Compat.convertStaffCollectionToNewUser(collection);
            if (!TextUtils.isEmpty(eventId)) {
                setEventPersonal(u.getId());
            } else {
                setAllEventPersonal(u.getId());
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
             /*用户单选, 负责人*/
            case FinalVariables.REQUEST_ONLY:
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
                        showLoading("");
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
        for (int i = 0; i < detail.sheetEventsSupporter.size(); i++) {
            eventIsList.add(detail.sheetEventsSupporter.get(i).id);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("responsorId", userId);
        map.put("eventIds", eventIsList);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                setAllEventPerson(map, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("设置all事件负责人：", response);
                        showLoading("");
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

    /**
     * 转移\分派
     */
    @Subscribe
    public void onWorkSheetDetailsRush(WorksheetEventChangeEvent event) {

        getData();
    }


    /**
     * 重做回调
     */
    @Subscribe
    public void onWorkSheetDetailsRedo(WorksheetInfo event) {

        getData();
    }

    /**
     * 提交完成 回调
     */
    @Subscribe
    public void onWorkSheetDetailsFinish(WorksheetDetail event) {

        getData();
    }

}
