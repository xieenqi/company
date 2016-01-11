package com.loyo.oa.v2.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.WeeksDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.activity_workreports_edit)
public class WorkReportEditActivity extends BaseActivity {

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_reviewer;
    @ViewById ViewGroup layout_toUser;
    @ViewById ViewGroup layout_time;
    @ViewById ViewGroup layout_del;
    @ViewById EditText edt_content;
    @ViewById RadioGroup rg;
    @ViewById TextView tv_time;
    @ViewById TextView tv_toUser;
    @ViewById TextView tv_reviewer;
    @ViewById ImageView img_title_toUser;

    String cc_user_id = null;
    String cc_department_id = null;
    String cc_user_name = null;
    String cc_department_name = null;

    String mBeginAt, mEndAt;
    int mSelectType = 1;

    NewUser user_reviewer;
    WeeksDialog weeksDialog = null;

    @Extra("mWorkReport")
    WorkReport mWorkReport;

    @AfterViews
    void initUI() {
        super.setTitle("编辑工作报告");

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left.setOnTouchListener(touch);
        img_title_right.setOnTouchListener(touch);
        layout_reviewer.setOnTouchListener(touch);
        layout_toUser.setOnTouchListener(touch);
        layout_del.setOnTouchListener(touch);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb1:
                        //fixes bug290 ,ykb 07-13
                        if(mWorkReport.getType()!=WorkReport.DAY){
                            mBeginAt = DateTool.getBeginAt_ofDay(app.df_api_get2);
                            mEndAt = DateTool.getEndAt_ofDay(app.df_api_get2);
                            tv_time.setText(DateTool.getDate(mBeginAt, app.df_api_get2, app.df5));
                        }
                        mSelectType = WorkReport.DAY;
                        break;
                    case R.id.rb2:
                        //fixes bug290 ,ykb 07-13
                        if(mWorkReport.getType()!=WorkReport.WEEK) {
                            mBeginAt = DateTool.getBeginAt_ofWeek(app.df_api_get2);
                            mEndAt = DateTool.getEndAt_ofWeek(app.df_api_get2);
                            tv_time.setText(weeksDialog.GetDefautlText());
                        }
                        mSelectType = WorkReport.WEEK;
                        break;
                    case R.id.rb3:
                        //fixes bug290 ,ykb 07-13
                        if(mWorkReport.getType()!=WorkReport.MONTH) {
                            mBeginAt = DateTool.getBeginAt_ofMonth(app.df_api_get2, 8);
                            mEndAt = DateTool.getEndAt_ofMonth(app.df_api_get2);
                            DateTool.calendar = Calendar.getInstance();
                            int year = DateTool.calendar.get(Calendar.YEAR);
                            int month = DateTool.calendar.get(Calendar.MONTH);
                            tv_time.setText(year + "-" + String.format("%02d", (month + 1)));
                        }
                        mSelectType = WorkReport.MONTH;
                        break;
                }
                mWorkReport.setType(mSelectType);
            }
        });

        if (weeksDialog == null) {
            weeksDialog = new WeeksDialog(tv_time);
        }

        try {
//            mBeginAt = mWorkReport.getBeginAt();
//            mEndAt = mWorkReport.getEndAt();

            Date begin, end;

            edt_content.setText(mWorkReport.getContent());
            begin = app.df_api_get2.parse(mBeginAt);
            end = app.df_api_get2.parse(mEndAt);

            switch (mWorkReport.getType()) {
                case WorkReport.DAY:
                    rg.check(R.id.rb1);
                    tv_time.setText(app.df5.format(begin));
                    break;
                case WorkReport.WEEK:
                    rg.check(R.id.rb2);

                    tv_time.setText(app.df7.format(begin).concat(" - ")
                            .concat(app.df7.format(end)));
                    break;
                case WorkReport.MONTH:
                    rg.check(R.id.rb3);
                    tv_time.setText(app.df8.format(begin));
                    break;
            }
            if (mWorkReport.getReviewer() != null) {
                setReviewer(mWorkReport.getReviewer().getUser());
            }

//            cc_user_id = mWorkReport.getMentionedUserIds();
//            cc_department_id = mWorkReport.getMentionedDeptIds();
            tv_toUser.setText(Common.getDeptsUsersName(cc_department_id, cc_user_id));
        } catch (Exception ex) {
            Global.ProcException(ex);
        }

    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_time,
            R.id.layout_reviewer, R.id.layout_toUser, R.id.layout_del})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:
                String content = edt_content.getText().toString().trim();
                if ("".equals(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    break;
                }
                if (user_reviewer == null) {
                    Toast(getString(R.string.review_user) + getString(R.string.app_no_null));
                    break;
                }

                JSONObject jsonObject = new JSONObject();
                StringEntity stringEntity = null;
                try {
//                  jsonObject.put("title", mWorkReport.getName());
                    jsonObject.put("content", content);
                    jsonObject.put("reportType", mSelectType);
                    //fixes bug290 ,ykb 07-13
                    String beginAt=DateTool.getDate(mBeginAt, app.df2, app.df_api);
                    String endAt=DateTool.getDate(mEndAt, app.df2, app.df_api);
                    if(TextUtils.isEmpty(beginAt)){
                        beginAt=DateTool.formateServerDate(mBeginAt, app.df_api);
                    }
                    if(TextUtils.isEmpty(endAt)){
                        endAt=DateTool.formateServerDate(mEndAt, app.df_api);
                    }
                    jsonObject.put("mDeadline",beginAt);
                    jsonObject.put("endAt", endAt);

                    JSONObject jsonObject_reviewer = new JSONObject();
                    jsonObject_reviewer.put("id", user_reviewer.getId());
                    jsonObject.put("reviewer", jsonObject_reviewer);
                    if (cc_department_id != null) {
                        jsonObject.put("mentionedDeptIds", cc_department_id);
                    }
                    if (cc_user_id != null) {
                        jsonObject.put("mentionedUserIds", cc_user_id);
                    }

                    stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                app.logUtil.d("stringEntity.toString():" + jsonObject.toString());

                ServerAPI.request(WorkReportEditActivity.this, ServerAPI.PUT, FinalVariables.workreports + mWorkReport.getId(), stringEntity, ServerAPI.CONTENT_TYPE_JSON, AsyncHttpResponseHandler_workreports.class);
                break;
            case R.id.layout_time:
                //选择日期
                selectDate();
                break;
            case R.id.layout_reviewer:
                Bundle bundle = new Bundle();
                bundle.putInt(DepartmentUserActivity.STR_SELECT_TYPE, DepartmentUserActivity.TYPE_SELECT_SINGLE);
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, bundle);
                break;
            case R.id.layout_toUser:
                app.startActivityForResult(this, DepartmentUserActivity.class, MainApp.ENTER_TYPE_RIGHT, DepartmentUserActivity.request_Code, null);
                break;
            case R.id.layout_del:
                cc_user_id = null;
                cc_department_id = null;
                cc_user_name = null;
                cc_department_name = null;

                tv_toUser.setText("");
                layout_del.setVisibility(View.GONE);
                img_title_toUser.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case DepartmentUserActivity.request_Code:
                         /*抄送*/
                cc_department_id = data.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_ID);
                cc_department_name = data.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_NAME);
                cc_user_id = data.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                cc_user_name = data.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                String cc = null;
                if (cc_department_name != null && cc_user_name != null) {
                    cc = cc_department_name + "," + cc_user_name;
                } else if (cc_department_name != null) {
                    cc = cc_department_name;
                } else if (cc_user_name != null) {
                    cc = cc_user_name;
                }

                if (cc != null) {
                    tv_toUser.setText(cc);
                    layout_del.setVisibility(View.VISIBLE);
                    img_title_toUser.setVisibility(View.GONE);
                }

                /*点评人*/
                User user = (User) data.getSerializableExtra(User.class.getName());
                if (user != null) {
                    user_reviewer = user.toShortUser();
                    tv_reviewer.setText(user.getRealname());
                }

                break;

        }
    }

    void setReviewer(NewUser user) {
        user_reviewer = user;
        tv_reviewer.setText(user_reviewer.getRealname());
    }

    public class AsyncHttpResponseHandler_workreports extends BaseActivityAsyncHttpResponseHandler {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                WorkReport workReport = MainApp.gson.fromJson(getStr(arg2), WorkReport.class);

                Toast("修改" + getString(R.string.app_succeed));
                if (workReport != null) {
                    workReport.setAck(true);

                    Intent intent = getIntent();
                    intent.putExtra("review", workReport);
                    app.finishActivity(WorkReportEditActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }

            } catch (Exception e) {
                Global.ProcException(e);
            }
        }
    }

    void selectDate() {
        DateTool.calendar = Calendar.getInstance();

        switch (mSelectType) {
            case WorkReport.DAY:
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String str = year + "-" + String.format("%02d", (monthOfYear + 1)) + "-"
                                + String.format("%02d", dayOfMonth);

                        tv_time.setText(str);
                        mBeginAt = str.concat(" 09:00");
                        mEndAt = str.concat(" 23:59");
                    }
                },
                        DateTool.calendar.get(Calendar.YEAR),
                        DateTool.calendar.get(Calendar.MONTH),
                        DateTool.calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case WorkReport.MONTH:
                DatePickerDialog datePickerDialogMonth = new DatePickerDialog(
                        this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String str = year + "-" + String.format("%02d", (monthOfYear + 1));

                        tv_time.setText(str);
                        mBeginAt = str.concat("-01 09:00");

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear + 2, 1);
                        calendar.set(Calendar.DAY_OF_MONTH, -1);

                        mEndAt = str.concat("-").concat(String.valueOf(calendar.get(Calendar.DATE))).concat(" 23:59");
                    }
                },
                        DateTool.calendar.get(Calendar.YEAR), DateTool.calendar.get(Calendar.MONTH),
                        DateTool.calendar.get(Calendar.DAY_OF_MONTH));

                ((LinearLayout) ((ViewGroup) datePickerDialogMonth.getDatePicker().getChildAt(0))
                        .getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

                datePickerDialogMonth.setTitle("选择月份");
                datePickerDialogMonth.show();
                break;
            case WorkReport.WEEK:
                weeksDialog.showChoiceDialog("选择周报").show();
                break;
        }
    }

}
