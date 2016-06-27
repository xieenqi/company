package com.loyo.oa.v2.activity.attendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SignInGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    TextView tv_title_1;
    @ViewById
    TextView tv_time;
    @ViewById
    TextView tv_count_time;
    @ViewById
    TextView tv_count_time2;
    @ViewById
    TextView tv_address;
    @ViewById
    TextView tv_result;
    @ViewById
    ImageView iv_refresh_address;
    @ViewById
    EditText et_reason;
    @ViewById
    ViewGroup layout_reason;
    @ViewById
    GridView gridView_photo;
    @Extra
    AttendanceRecord mAttendanceRecord;
    @Extra("isPopup")
    boolean isPopup;
    @Extra("needPhoto")
    boolean NeedPhoto;
    @Extra("outKind")
    int outKind; //0上班 1正常下班 2完成加班
    @Extra("serverTime")
    long serverTime;//当前时间
    @Extra("extraWorkStartTime")
    long extraWorkStartTime;//加班开始时间

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
    private static String tvTimeName;

    public static final int CLOCKIN_STATE_NO = 1; //上班打卡状态
    public static final int CLOCKIN_STATE_OFF = 1; //下班打卡状态
    public static final int CLOCKIN_STATE_OVERTIME = 5; //加班班打卡状态

    @Override
    public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
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


    private static final class MHandler extends Handler {
        private WeakReference<AttendanceAddActivity> mActivity;
        private static final int TEXT_LEN = 6;

        private MHandler(final AttendanceAddActivity activity) {
            mActivity = new WeakReference<AttendanceAddActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String time = String.valueOf(msg.what);
            String des = "请在".concat(time).concat("秒内完成打卡");
            SpannableStringBuilder builder = Utils.modifyTextColor(des, Color.parseColor("#f5625a"), des.length() - TEXT_LEN - time.length(), des.length() - TEXT_LEN);

            TextView tvtime2 = mActivity.get().tv_count_time2;
            TextView tvtime = mActivity.get().tv_count_time;
            if ("加班时间:".equals(tvTimeName)) {
                tvtime2.setVisibility(View.VISIBLE);
                tvtime.setVisibility(View.GONE);
                if (null != tvtime2) {
                    tvtime2.setText(builder);
                }
            } else {
                if (null != tvtime) {
                    tvtime.setText(builder);
                }
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
        switch (outKind) {
            case 0:
                state = CLOCKIN_STATE_NO;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("上班打卡");
                break;
            case 1:
                state = CLOCKIN_STATE_OFF;
                tvTimeName = "打卡时间:";
                tv_title_1.setText("下班打卡");
                break;
            case 2:
                state = CLOCKIN_STATE_OVERTIME;
                tvTimeName = "加班时间:";
                tv_title_1.setText("加班打卡");
                break;
            default:

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
        /*完成加班*/
        if (outKind == 2) {
            et_reason.setHint("请输入加班原因");
            String time = (DateTool.timet(extraWorkStartTime + "", DateTool.DATE_FORMATE_TRANSACTION)
                    + "-" + DateTool.timet(serverTime + "", DateTool.DATE_FORMATE_TRANSACTION));
            SpannableStringBuilder builder = Utils.modifyTextColor(time, getResources().getColor(R.color.green51), 5, time.length());
            tv_time.setText(tvTimeName + builder);
            tv_time.setTextColor(getResources().getColor(R.color.green51));
        } else {/*正常上下班*/
            String time = tvTimeName.concat(app.df6.format(new Date(mAttendanceRecord.getCreatetime() * 1000)));
            SpannableStringBuilder builder = Utils.modifyTextColor(time, getResources().getColor(R.color.green51), 5, time.length());
            tv_time.setText(builder);
            if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE || mAttendanceRecord.getState() == AttendanceRecord.STATE_LEAVE_EARLY) {
                if (mAttendanceRecord.getState() == AttendanceRecord.STATE_BE_LATE) {
                    et_reason.setHint("请输入迟到原因");
                    state = 2;
                } else {
                    et_reason.setHint("请输入早退原因");
                    state = 3;
                }
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
            public void success(final ArrayList<Attachment> _attachments, final Response response) {
                HttpErrorCheck.checkResponse(response);
                attachments = _attachments;
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
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
    void onClick(final View v) {
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

                /*暂时取消外勤判断 */
                /*if (mAttendanceRecord.getOutstate() != AttendanceRecord.OUT_STATE_OFFICE_WORK
                        && mAttendanceRecord.getState() != 5) {
                    showOutAttendanceDialog();
                } else {
                    commitAttendance();
                }*/

                commitAttendance();
                break;

            case R.id.iv_refresh_address:
                iv_refresh_address.startAnimation(animation);
                new LocationUtilGD(this, this);
                break;
            default:

                break;
        }
    }

    /**
     * 检查提交的数据
     *
     * @return
     */
    private boolean check() {

        if (TextUtils.isEmpty(et_reason.getText().toString())) {
            int state = mAttendanceRecord.getState();
            if (state == AttendanceRecord.STATE_OVERWORK) {
                if (outKind == 2) {
                    Toast("加班原因不能为空");
                    return false;
                }
            } else if (state == AttendanceRecord.STATE_LEAVE_EARLY) {
                Toast("早退原因不能为空");
                return false;
            } else if (state == AttendanceRecord.STATE_BE_LATE) {
                Toast("迟到原因不能为空");
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
    private void refreshLocation(final double longitude, final double latitude) {
        String originalgps = longitude + "," + latitude;
        app.getRestAdapter().create(IAttendance.class).refreshLocation(originalgps, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                String address = tv_address.getText().toString();
                mAttendanceRecord.setAddress(address);
            }
        });
    }

    /**
     * 显示打卡超时对话框
     */
    private void showTimeOutDialog() {
        showGeneralDialog(false, false, getString(R.string.app_attendance_outtime_message));
        generalPopView.setNoCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                onBackPressed();
                finish();
            }
        });

    }

    /**
     * 弹出外勤确认对话框
     */
    private void showOutAttendanceDialog() {
        showGeneralDialog(true, true, getString(R.string.app_attendance_out_message));
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                commitAttendance();
            }
        });

        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
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
        map.put("originalgps", mAttendanceRecord.getOriginalgps());
        map.put("gpsinfo", mAttendanceRecord.getGpsinfo());
//        map.put("address", mAttendanceRecord.getAddress());
        map.put("address", tv_address.getText().toString());
        map.put("reason", reason);
        map.put("state", state);
        map.put("outstate", mAttendanceRecord.getOutstate());
        map.put("extraWorkStartTime", extraWorkStartTime);
        map.put("extraWorkEndTime", serverTime);

        map.put("confirmExtraTime", mAttendanceRecord.getConfirmExtraTime());
        map.put("confirmtime", mAttendanceRecord.getConfirmtime());
        map.put("extraState", mAttendanceRecord.getExtraState());
        map.put("extraTime", mAttendanceRecord.getExtraTime());
        map.put("leaveDays", mAttendanceRecord.getLeaveDays());
        map.put("remainTime", mAttendanceRecord.getRemainTime());
        map.put("tagstate", mAttendanceRecord.getTagstate());


        if (isPopup) {
            if (outKind == 1) {
                map.put("extraChooseState", 1);
            } else if (outKind == 2) {
                map.put("extraChooseState", 2);
            }
        }
        if (attachments.size() != 0) {
            map.put("attachementuuid", uuid);
        }
        LogUtil.d("提交考勤:" + MainApp.gson.toJson(map));
        app.getRestAdapter().create(IAttendance.class).confirmAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(final AttendanceRecord attendanceRecord, final Response response) {
                try {
                    Toast("打卡成功!");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
//                if (error.getKind() == RetrofitError.Kind.NETWORK) {
//                    Toast("请检查您的网络连接");
//                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
//                    if (error.getResponse().getStatus() == 500) {
//                        Toast("网络异常500，请稍候再试");
//                        try {
//                            LogUtil.dll("error:" + Utils.convertStreamToString(error.getResponse().getBody().in()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
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
    void onDealImageResult(final Intent data) {
        if (null == data) {
            return;
        }
        Utils.dialogShow(this, "请稍候");
        final Attachment delAttachment = (Attachment) data.getSerializableExtra("delAtm");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("bizType", 0);
        map.put("uuid", uuid);

        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).remove(String.valueOf(delAttachment.getId()), map, new RCallback<Attachment>() {
            @Override
            public void success(final Attachment attachment, final Response response) {
                Utils.dialogDismiss();
                Toast("删除附件成功!");
                attachments.remove(delAttachment);
                init_gridView_photo();
            }

            @Override
            public void failure(final RetrofitError error) {
                Utils.dialogDismiss();
                HttpErrorCheck.checkError(error);
                Toast("删除附件失败!");
                super.failure(error);
            }
        });
    }

    @OnActivityResult(SelectPicPopupWindow.GET_IMG)
    void onGetImageResult(final Intent data) {
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
                            public void onNext(final Serializable serializable) {
                                getAttachments();
                            }

                            @Override
                            public void onError(final Throwable e) {
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
