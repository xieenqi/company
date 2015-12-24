package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.DayofAttendance;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :查看考勤界面
 * 作者 : ykb
 * 时间 : 15/9/16.
 */
@EActivity(R.layout.activity_attendance_info)
public class PreviewAttendanceActivity extends BaseActivity {
    @ViewById ViewGroup layout_back;
    @ViewById TextView tv_title;

    @ViewById RoundImageView iv_avartar;

    @ViewById TextView tv_name;
    @ViewById TextView tv_role;

    @ViewById ImageView iv_type;

    @ViewById TextView tv_info;
    @ViewById TextView tv_reason;

    @ViewById GridView gridView_photo;
    @ViewById Button btn_confirm;

    @Extra("data") DayofAttendance attendance;
    @Extra int inOrOut;

    private SignInGridViewAdapter adapter;
    private ArrayList<Attachment> attachments = new ArrayList<>();

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("考勤详情");
        initGridView();
        initData();
        LogUtil.d(inOrOut+" 考勤详情 传递 数据："+ MainApp.gson.toJson(attendance));
    }

    /**
     * 获取附件
     * @param record
     */
    private void getAttachments(AttendanceRecord record) {
        if (TextUtils.isEmpty(record.getAttachementuuid())){//附件id为空
            return;
        }
        Utils.getAttachments(record.getAttachementuuid(), new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> _attachments, Response response) {
                attachments = _attachments;
                initGridView();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                Toast("获取附件失败");
            }
        });
    }
    /**
     * 初始化数据
     */
    private void initData() {
        if (null == attendance) {
            return;
        }
        AttendanceRecord record =inOrOut== ValidateItem.ATTENDANCE_STATE_OUT?attendance.getOut():attendance.getIn();
        ImageLoader.getInstance().displayImage(attendance.getUser().avatar, iv_avartar);
        final User user = attendance.getUser();

        tv_name.setText(user.getRealname());
        String deptName= TextUtils.isEmpty(user.departmentsName)? Common.getDepartment(user.depts.get(0).getShortDept().getId()).getName():user.departmentsName;
        tv_role.setText(deptName + " " + (null == user.shortPosition || TextUtils.isEmpty(user.shortPosition.getName()) ? "" : user.shortPosition.getName()));

        if (record.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
            iv_type.setImageResource(R.drawable.icon_field_work_unconfirm);
            btn_confirm.setVisibility(View.VISIBLE);
        } else if (record.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
            iv_type.setImageResource(R.drawable.icon_field_work_confirm);
        }else if (record.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
            iv_type.setImageResource(R.drawable.icon_office_work);
        }
        String info="";
        if (record.getState() == AttendanceRecord.STATE_BE_LATE) {
            info = "上班迟到, ";
        }else if(record.getState() == AttendanceRecord.STATE_LEAVE_EARLY){
            info = "下班早退, ";
        }
        String content=info+"打卡时间: " + app.df3.format(new Date(record.getCreatetime()* 1000));//
        if(!TextUtils.isEmpty(info)) {
            tv_info.setText(Utils.modifyTextColor(content, Color.RED, 2, 4));
        }else {
            tv_info.setText(content);
        }
        tv_reason.setText(record.getReason());
         if(user.id.equals(MainApp.user.id)){//自己不能确认外勤
            btn_confirm.setVisibility(View.GONE);
        }
        getAttachments(record);
    }


    /**
     * 初始化附件
     */
    private void initGridView() {
        if (null == adapter) {
            adapter = new SignInGridViewAdapter(this, attachments, false, false);
        } else {
            adapter.setDataSource(attachments);
        }
        SignInGridViewAdapter.setAdapter(gridView_photo, adapter);
    }

    @Click(R.id.layout_back)
    void back() {
        finish();
        //onBackPressed();
    }
    /**
     * 确认外勤的点击监听
     */
    @Click(R.id.btn_confirm)
    void confirmFieldWork() {
        showConfirmOutAttendanceDialog();
    }

    /**
     * 弹出外勤确认对话框
     */
    private void showConfirmOutAttendanceDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_out_attendance, null, false);
        ((TextView) dialogView.findViewById(R.id.tv_content)).setText("是否确定该员工的外勤?\r\n确认后将无法取消!");
        dialogView.getBackground().setAlpha(150);
        final PopupWindow dialog = new PopupWindow(dialogView, -1, -1, true);
        dialog.setAnimationStyle(R.style.PopupAnimation);
        dialog.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        dialog.showAtLocation(findViewById(R.id.tv_title), Gravity.BOTTOM, 0, 0);


        TextView confirm = (TextView) dialogView.findViewById(R.id.btn_confirm);
        TextView cancel = (TextView) dialogView.findViewById(R.id.btn_cancel);

        confirm.setOnTouchListener(Global.GetTouch());
        cancel.setOnTouchListener(Global.GetTouch());

        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.dismiss();
                return false;
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                confirmOutAttendance();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认外勤
     */
    private void confirmOutAttendance(){
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class).confirmOutAttendance(attendance.getIn().getId(), new RCallback<AttendanceRecord>() {
            @Override
            public void success(AttendanceRecord record, Response response) {
                btn_confirm.setVisibility(View.GONE);
                attendance.setIn(record);

                if (record.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                    iv_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                } else if (record.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                    iv_type.setImageResource(R.drawable.icon_field_work_confirm);
                } else if (record.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
                    iv_type.setImageResource(R.drawable.icon_office_work);
                }

                Intent intent = new Intent();
                intent.putExtra("data", attendance);
                setResult(RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                Toast("确认外勤失败");
            }
        });
    }


}
