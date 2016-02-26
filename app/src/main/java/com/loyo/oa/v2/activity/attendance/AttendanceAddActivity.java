package com.loyo.oa.v2.activity.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.AttendancePhoto;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :新增考勤界面
 * 作者 : ykb
 * 时间 : 15/9/14.
 */
@EActivity(R.layout.activity_attendance_add)
public class AttendanceAddActivity extends BaseActivity implements LocationUtilGD.AfterLocation {

    //控件
    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById TextView tv_title_1;
    @ViewById TextView tv_time;
    @ViewById TextView tv_count_time;
    @ViewById TextView tv_address;
    @ViewById TextView tv_result;
    @ViewById ImageView iv_refresh_address;
    @ViewById EditText et_reason;
    @ViewById ViewGroup layout_reason;
    @ViewById GridView gridView_photo;
    @Extra AttendanceRecord mAttendanceRecord;
    @Extra("needExtra") boolean needExtra;
    @Extra("needPhoto") boolean NeedPhoto;
    @Extra("outKind") int outKind; //0上班 1正常下班 2完成加班
    @Extra("serverTime") long serverTime;//当前时间
    @Extra("extraStartTime") long extraStartTime;//加班开始时间

    //附件相关
    private SignInGridViewAdapter adapter;
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String uuid = StringUtil.getUUID();
    private int state;

    //打卡计时相关
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isRun;
    private MHandler mHandler = new MHandler(this);
    private Animation animation;
    private String tvTimeName;

    @Override
    public void OnLocationGDSucessed(String address, double longitude, double latitude, String radius) {
        iv_refresh_address.clearAnimation();
        animation.reset();
        tv_address.setText(address);
        refreshLocation(longitude, latitude);
        LocationUtilGD.sotpLocation();
    }

    @Override
    public void OnLocationGDFailed() {
        Toast("刷新位置失败");
        iv_refresh_address.clearAnimation();
        animation.reset();
        LocationUtilGD.sotpLocation();
    }

    private static class MHandler extends Handler {
        private WeakReference<AttendanceAddActivity> mActivity;

        private MHandler(AttendanceAddActivity activity) {
            mActivity = new WeakReference<AttendanceAddActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String time = String.valueOf(msg.what);
            String des = "请在".concat(time).concat("秒内完成打卡");
            SpannableStringBuilder builder = Utils.modifyTextColor(des, Color.RED, des.length() - 6 - time.length(), des.length() - 6);
            TextView tvtime = mActivity.get().tv_count_time;
            if (null != tvtime) {
                tvtime.setText(builder);
            }
            if (0 == msg.what) {
                mActivity.get().recycle();
                mActivity.get().showTimeOutDialog();
            }
        }
    }

    @AfterViews
    void initViews() {

        setTouchView(NO_SCROLL);
        switch (outKind){
            case 0:
                state = 1;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("上班打卡");
                break;

            case 1:
                state = 1;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("下班打卡");
                break;

            case 2:
                state = 5;
                tvTimeName = "加班时间:";
                tv_title_1.setText("加班打卡");
                break;
        }

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        iv_refresh_address.setOnTouchListener(Global.GetTouch());
        animation = AnimationUtils.loadAnimation(this, R.anim.rotateanimation);
        initData();
    }


    /**
     * 开始倒计时
     */
    private void countDown() {
        mTimerTask = new TimerTask() {
            private int seconds = mAttendanceRecord.getRemainTime() * 60;
            @Override
            public void run() {
                if (!isRun) {
                    return;
                }
                seconds--;
                mHandler.sendEmptyMessage(seconds);
            }
        };

        isRun = true;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
    }

    /**
     * 回收计时器
     */
    private void recycle() {
        isRun = false;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == mAttendanceRecord) {
            return;
        }

        tv_result.setVisibility(mAttendanceRecord.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK ? View.GONE : View.VISIBLE);
        String result = mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK ? "您已超出内勤范围,本次打卡将记作外勤!" : "";
        tv_result.setText(result);
        tv_address.setText(mAttendanceRecord.getAddress());

        if(outKind == 2){

            et_reason.setHint("请输入加班原因");
            layout_reason.setVisibility(View.VISIBLE);
            String time = (DateTool.timet(extraStartTime+"",DateTool.DATE_FORMATE_TRANSACTION)
                    +"-"+DateTool.timet(serverTime+"",DateTool.DATE_FORMATE_TRANSACTION));
            SpannableStringBuilder builder = Utils.modifyTextColor(time, Color.GREEN, 5, time.length());
            tv_time.setText(tvTimeName+builder);
            tv_time.setTextColor(Color.GREEN);
        }else {
            String time = tvTimeName.concat(app.df6.format(new Date(mAttendanceRecord.getCreatetime() * 1000)));
            SpannableStringBuilder builder = Utils.modifyTextColor(time, Color.GREEN, 5, time.length());
            tv_time.setText(builder);
            if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE || mAttendanceRecord.getState() == AttendanceRecord.STATE_LEAVE_EARLY) {
                if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE) {
                    et_reason.setHint("请输入迟到原因");
                    state = 2;
                } else {
                    et_reason.setHint("请输入早退原因");
                    state = 3;
                }
                layout_reason.setVisibility(View.VISIBLE);
            }
        }
        init_gridView_photo();
        countDown();
    }

    /**
     * 获取附件
     */
    private void getAttachments() {
        Utils.getAttachments(uuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> _attachments, Response response) {
                attachments = _attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                Toast("获取附件失败");
            }
        });
    }

    /**
     * 初始化附件列表
     */
    private void init_gridView_photo() {
        if (null == adapter) {
            adapter = new SignInGridViewAdapter(this, attachments, true, true, ExtraAndResult.FROMPAGE_ATTENDANCE);
        } else {
            adapter.setDataSource(attachments);
        }
        SignInGridViewAdapter.setAdapter(gridView_photo, adapter);
    }

    @Click({R.id.img_title_left, R.id.img_title_right, R.id.iv_refresh_address})
    void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                onBackPressed();
                break;

            case R.id.img_title_right:
                if (!check()) {
                    return;
                }

                if (NeedPhoto && attachments.size() == 0) {
                    Toast("需要考勤照片，请拍照");
                    return;
                }

                if (mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK
                        && mAttendanceRecord.getState() != 5) {
                        showOutAttendanceDialog();
                } else {
                        commitAttendance();
                }

                break;

            case R.id.iv_refresh_address:
                iv_refresh_address.startAnimation(animation);
                new LocationUtilGD(this, this);
                break;
        }
    }

    /**
     * 检查提交的数据
     *
     * @return
     */
    private boolean check() {

        if(outKind == 2 && needExtra){
            if(layout_reason.getVisibility() == View.VISIBLE && TextUtils.isEmpty(et_reason.getText().toString())){
                Toast("加班原因不能为空");
                return false;
            }
        }

        else if(outKind != 2){
            if(layout_reason.getVisibility() == View.VISIBLE && TextUtils.isEmpty(et_reason.getText().toString())){
                    if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE) {
                        Toast("迟到原因不能为空");
                    } else {
                        Toast("早退原因不能为空");
                    }
                    return false;
            }
        }

        if (TextUtils.isEmpty(tv_address.getText().toString())) {
            Toast("地址不能为空");
            return false;
        }
        return true;
    }

    /**
     * 刷新打卡位置
     *
     * @param longitude
     * @param latitude
     */
    private void refreshLocation(double longitude, double latitude) {
        String originalgps = longitude + "," + latitude;
        app.getRestAdapter().create(IAttendance.class).refreshLocation(originalgps, new RCallback<Object>() {
            @Override
            public void success(Object o, Response response) {
                String address = tv_address.getText().toString();
                mAttendanceRecord.setAddress(address);
            }
        });
    }

    /**
     * 显示打卡超时对话框
     */
    private void showTimeOutDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_out_attendance, null, false);
        dialogView.findViewById(R.id.layout_btn).setVisibility(View.GONE);
        ((TextView) dialogView.findViewById(R.id.tv_content)).setText("打卡操作超时,请重新打卡");
        dialogView.getBackground().setAlpha(150);
        final PopupWindow dialog = new PopupWindow(dialogView, -1, -1, true);
        dialog.setAnimationStyle(R.style.PopupAnimation);
        dialog.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        dialog.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

        dialogView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.dismiss();
                return false;
            }
        });

        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                onBackPressed();
            }
        });
    }

    /**
     * 弹出外勤确认对话框
     */
    private void showOutAttendanceDialog() {

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_out_attendance, null, false);
        dialogView.getBackground().setAlpha(150);
        final PopupWindow dialog = new PopupWindow(dialogView, -1, -1, true);
        dialog.setAnimationStyle(R.style.PopupAnimation);
        dialog.setBackgroundDrawable(new BitmapDrawable(getResources()));// 响应键盘三个主键的必须步骤
        dialog.showAtLocation(findViewById(R.id.tv_title_1), Gravity.BOTTOM, 0, 0);

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
                commitAttendance();
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
     * 提交考勤
     */
    private void commitAttendance() {

        String reason = et_reason.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("inorout", mAttendanceRecord.getInorout());
        map.put("checkindate", mAttendanceRecord.getCheckindate());
        map.put("createtime", mAttendanceRecord.getCreatetime());
        map.put("attachementuuid", uuid);
        map.put("originalgps", mAttendanceRecord.getOriginalgps());
        map.put("gpsinfo", mAttendanceRecord.getGpsinfo());
        map.put("address", mAttendanceRecord.getAddress());
        map.put("reason", reason);
        map.put("state", state);
        map.put("outstate", mAttendanceRecord.getOutstate());
        map.put("extraWorkStartTime",extraStartTime);
        map.put("extraWorkEndTime",serverTime);

        LogUtil.dll("提交考勤:"+MainApp.gson.toJson(map));
        app.getRestAdapter().create(IAttendance.class).confirmAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(AttendanceRecord attendanceRecord, Response response) {
                Toast("打卡成功!");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                onBackPressed();
                try {
                    LogUtil.dll("result:" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    Toast("请检查您的网络连接");
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    if (error.getResponse().getStatus() == 500) {
                        Toast("网络异常500，请稍候再试");
                        try {
                            LogUtil.dll("error:" + Utils.convertStreamToString(error.getResponse().getBody().in()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        recycle();
        //关闭键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(findViewById(R.id.tv_address).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.finish();
    }


    /*附件删除回调*/
    @OnActivityResult(FinalVariables.REQUEST_DEAL_ATTACHMENT)
    void onDealImageResult(Intent data) {
        if (null == data) {
            return;
        }
        Utils.dialogShow(this, "请稍候");
        final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), new RCallback<Attachment>() {
            @Override
            public void success(Attachment attachment, Response response) {
                Utils.dialogDismiss();
                Toast("删除附件成功!");
                attachments.remove(delAttachment);
                init_gridView_photo();
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.dialogDismiss();
                HttpErrorCheck.checkError(error);
                Toast("删除附件失败!");
                super.failure(error);
            }
        });
    }

    @OnActivityResult(SelectPicPopupWindow.GET_IMG)
    void onGetImageResult(Intent data) {
        if (null == data) {
            return;
        }
        try {
            ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = (ArrayList<SelectPicPopupWindow.ImageInfo>) data.getSerializableExtra("data");
            for (SelectPicPopupWindow.ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(this, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        Utils.uploadAttachment(uuid, 0, newFile).subscribe(new CommonSubscriber(this) {
                            @Override
                            public void onNext(Serializable serializable) {
                                getAttachments();
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                Toast("网络异常");
                            }
                        });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }
}
