package com.loyo.oa.v2.activityui.attendance;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;
import com.loyo.oa.v2.activityui.attendance.presenter.impl.AttendanceDetailsPresenterImpl;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceDetailsView;
import com.loyo.oa.v2.activityui.signin.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import java.util.Date;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【考勤详情】
 *  Restruture by yyy on 16/10/12
 */
@EActivity(R.layout.activity_attendance_info)
public class AttendanceDetailsActivity extends BaseActivity implements AttendanceDetailsView{

    @ViewById
    ViewGroup layout_back;
    @ViewById
    TextView tv_title;
    @ViewById
    RoundImageView iv_avartar;
    @ViewById
    TextView tv_name;
    @ViewById
    TextView tv_role;
    @ViewById
    TextView tv_address_info;
    @ViewById
    ImageView iv_type;
    @ViewById
    TextView tv_info;
    @ViewById
    TextView tv_reason;
    @ViewById
    GridView gridView_photo;
    @ViewById
    Button btn_confirm;
    @ViewById
    LinearLayout ll_confirm;
    @ViewById
    TextView tv_confirmDept;
    @ViewById
    TextView tv_confirmName;
    @ViewById
    TextView tv_confirmTime;
    @ViewById
    TextView tv_explain;
    @ViewById
    TextView tv_message;

    @Extra("overTime")
    String overTime;
    @Extra("inOrOut")
    int inOrOut; // 1:上班 2:下班 3:加班
    @Extra(ExtraAndResult.EXTRA_ID)
    String attendanceId;

    private SignInGridViewAdapter adapter;
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String strMessage;
    private int type;

    private AttendanceDetailsPresenterImpl mPresenter;

    @AfterViews
    void initViews() {
        setTouchView(NO_SCROLL);
        layout_back.setOnTouchListener(Global.GetTouch());
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("考勤详情");
        mPresenter = new AttendanceDetailsPresenterImpl(this,AttendanceDetailsActivity.this);
        initGridView(attachments);
        mPresenter.getData(attendanceId);
    }

    @Click(R.id.layout_back)
    void back() {
        finish();
    }

    /**
     * 确认外勤(加班)的点击监听
     */
    @Click(R.id.btn_confirm)
    void confirmFieldWork() {
        showConfirmOutAttendanceDialog(strMessage);
    }

    /**
     * 弹出外勤(加班)确认对话框
     */
    private void showConfirmOutAttendanceDialog(final String str) {

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                mPresenter.confirmOutAttendance(attendanceId, type);
            }
        },"提示",str);
    }

    /**
     * 初始化附件
     */
    @Override
    public void initGridView(ArrayList<Attachment> mAttachments) {
        attachments = mAttachments;
        if (null == adapter) {
            adapter = new SignInGridViewAdapter(this, attachments, false, false, 0);
        } else {
            adapter.setDataSource(attachments);
        }
        SignInGridViewAdapter.setAdapter(gridView_photo, adapter);
    }

    /**
     * 初始化详情数据
     * */
    @Override
    public void initDetails(HttpAttendanceDetial mAttendanceDetails) {
        initData(mAttendanceDetails);
    }

    /**
     * 确认外勤加班处理
     * */
    @Override
    public void confirmOutEmbl() {
        btn_confirm.setVisibility(View.GONE);
        iv_type.setImageResource(R.drawable.icon_field_work_confirm);
        iv_type.setVisibility(View.VISIBLE);
        AppBus.getInstance().post(new AttendanceRecord());
        finish();
    }


    /**
     * 初始化数据
     */
    private void initData(HttpAttendanceDetial mAttendanceDetails) {
        if (null == mAttendanceDetails) {
            return;
        }

        ImageLoader.getInstance().displayImage(mAttendanceDetails.user.avatar, iv_avartar);
        final User user = mAttendanceDetails.user;
        tv_name.setText(user.getRealname());
        LogUtil.dll("考勤详情 员工信息:" + MainApp.gson.toJson(user));
        String deptName = "";
        if (null != Common.getDepartment(user.depts.get(0).getShortDept().getId())) {
            deptName = TextUtils.isEmpty(user.departmentsName) ?
                    Common.getDepartment(user.depts.get(0).getShortDept().getId()).getName() : user.departmentsName;
        }
        tv_role.setText(deptName + " " + (TextUtils.isEmpty(user.depts.get(0).getShortDept().getName())
                ? "-" : user.depts.get(0).getShortDept().title));

        /*确认加班*/
        if (mAttendanceDetails.state == 5 && mAttendanceDetails.extraState == AttendanceRecord.OUT_STATE_FIELD_OVERTIME) {
            btn_confirm.setVisibility(View.VISIBLE);
            btn_confirm.setText("确认加班");
            type = 1;
            strMessage = "是否确定该员工的加班?\n" + "确认后将无法取消！";
            /*确认外勤*/
        } else if (mAttendanceDetails.state != 5) {
            if (mAttendanceDetails.outstate == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                iv_type.setImageResource(R.drawable.icon_field_work_confirm);
                iv_type.setVisibility(View.VISIBLE);
                btn_confirm.setVisibility(View.VISIBLE);
                btn_confirm.setText("确认外勤");
                type = 2;
                strMessage = "是否确定该员工的外勤?\n" + "确认后将无法取消！";
            } else if (mAttendanceDetails.outstate == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                iv_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                iv_type.setVisibility(View.VISIBLE);
            }
        }

         /*加班处理*/
        if (mAttendanceDetails.state == 5 && inOrOut == 3) {
            String time = (DateTool.timet(mAttendanceDetails.extraWorkStartTime + "", DateTool.DATE_FORMATE_TRANSACTION)
                    + "-" + DateTool.timet(mAttendanceDetails.extraWorkEndTime + "", DateTool.DATE_FORMATE_TRANSACTION));
            tv_info.setText("时间：" + time + " 共" + overTime);
            tv_explain.setText("加班说明");
        } else { /*上班下班处理*/
            String info = "";
            if (mAttendanceDetails.state == AttendanceRecord.STATE_BE_LATE) {
                info = "上班迟到, ";
            } else if (mAttendanceDetails.state == AttendanceRecord.STATE_LEAVE_EARLY) {
                info = "下班早退, ";
            }
            String content = info + "打卡时间: " + app.df3.format(new Date(mAttendanceDetails.createtime * 1000));//
            if (!TextUtils.isEmpty(info)) {
                tv_info.setText(Utils.modifyTextColor(content, getResources().getColor(R.color.red1), 2, 4));
            } else {
                tv_info.setText(content);
            }
        }

        tv_address_info.setText(mAttendanceDetails.address);
        tv_reason.setText(TextUtils.isEmpty(mAttendanceDetails.reason) ? "正常考勤" : mAttendanceDetails.reason);
        if (user.id.equals(MainApp.user.id)) {//自己不能确认外勤
            btn_confirm.setVisibility(View.GONE);
        }

        mPresenter.getAttachments(mAttendanceDetails.attachementuuid);

        if (null != mAttendanceDetails.confirmuser) {
        /*已确认的外勤*/
            if (mAttendanceDetails.state != 4 && mAttendanceDetails.state != 5 &&
                    mAttendanceDetails.outstate == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                ll_confirm.setVisibility(View.VISIBLE);
                tv_confirmDept.setText(mAttendanceDetails.confirmuser.depts.get(0).getShortDept().getName());
                tv_confirmName.setText(mAttendanceDetails.confirmuser.name);
                tv_confirmTime.setText(app.df3.format(new Date(mAttendanceDetails.confirmtime * 1000)));
            } else if (mAttendanceDetails.state == 5 &&
                    mAttendanceDetails.extraState == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_OVERTIME) { /*已确认的加班*/
                ll_confirm.setVisibility(View.VISIBLE);
                tv_message.setText("确认加班");
                tv_confirmDept.setText(mAttendanceDetails.confirmuser.depts.get(0).getShortDept().getName());
                tv_confirmName.setText(mAttendanceDetails.confirmuser.name);
                tv_confirmTime.setText(app.df3.format(new Date(mAttendanceDetails.confirmtime * 1000)));
            }
        }
    }
}
