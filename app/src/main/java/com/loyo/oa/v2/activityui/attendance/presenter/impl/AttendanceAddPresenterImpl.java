package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceAddPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.CommonSubscriber;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新增考勤】Presenter
 * Created by yyy on 16/10/10.
 */

public class AttendanceAddPresenterImpl implements AttendanceAddPresenter {

    public Activity mActivity;
    public Context mContext;
    public AttendanceAddView crolView;
    public AttendanceRecord mAttendanceRecord;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isRun;
    private Handler mHandler;

    public AttendanceAddPresenterImpl(AttendanceRecord mAttendanceRecord, Context mContext, AttendanceAddView crolView, Activity mActivity){
        this.mContext = mContext;
        this.crolView = crolView;
        this.mActivity = mActivity;
        this.mAttendanceRecord = mAttendanceRecord;
    }

    @Override
    public Handler mHndler(final TextView tvtime,final TextView tvtime2,final String tvTimeName) {
        mHandler = new Handler(){
            int TEXT_LEN = 6;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String time = String.valueOf(msg.what);
                String des = "请在".concat(time).concat("秒内完成打卡");
                SpannableStringBuilder builder = Utils.modifyTextColor(des, Color.parseColor("#f5625a"), des.length() - TEXT_LEN - time.length(), des.length() - TEXT_LEN);

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

                if (isRun)
                    if (0 == msg.what) {
                        recycle();
                        crolView.showTimeOutDialog();
                    }
            }
        };
        return mHandler;
    }




    /**
     * 开始倒计时
     */
    @Override
    public void countDown() {
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
    @Override
    public void recycle() {
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
     * 刷新打卡位置
     * */
    @Override
    public void refreshLocation(final double longitude, final double latitude, final String address) {
        String originalgps = longitude + "," + latitude;
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).refreshLocation(originalgps, new RCallback<Object>() {
            @Override
            public void success(final Object o, final Response response) {
                mAttendanceRecord.setAddress(address);
            }
        });
    }

    /**
     * 获取附件
     * */
    @Override
    public void getAttachments(String uuid) {
        AttachmentService.getAttachments(uuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        crolView.setAttachmentEmbl(attachments);
                    }
                });
    }

    /**
     * 上传附件
     * */
    @Override
    public void uploadAttachments(final String uuid, ArrayList<ImageInfo> pickPhots) {
        try {
            for (ImageInfo item : pickPhots) {
                Uri uri = Uri.parse(item.path);
                File newFile = Global.scal(mActivity, uri);
                if (newFile != null && newFile.length() > 0) {
                    if (newFile.exists()) {
                        Utils.uploadAttachment(uuid, 0, newFile).subscribe(new CommonSubscriber(mActivity) {
                            @Override
                            public void onNext(final Serializable serializable) {
                                getAttachments(uuid);
                            }

                            @Override
                            public void onError(final Throwable e) {
                                super.onError(e);
                                crolView.showMsg("网络异常");
                            }
                        });
                    }
                }
            }
        } catch (Exception ex) {
            Global.ProcException(ex);
        }
    }

    /**
     * 提交考勤信息
     * */
    @Override
    public void commitAttendance(ArrayList<Attachment> mAttachment,boolean isPopup, int outKind,int state,String uuid,String address,String reason,long extraWorkStartTime,long serverTime,int lateMin,int earlyMin) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("inorout", mAttendanceRecord.getInorout());
        map.put("checkindate", mAttendanceRecord.getCheckindate());
        map.put("createtime", mAttendanceRecord.getCreatetime());
        map.put("originalgps", mAttendanceRecord.getOriginalgps());
        map.put("gpsinfo", mAttendanceRecord.getGpsinfo());
        map.put("address", address);
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

        map.put("lateMin", lateMin);
        map.put("earlyMin", earlyMin);

        if (isPopup) {
            if (outKind == 1) {
                map.put("extraChooseState", 1);
            } else if (outKind == 2) {
                map.put("extraChooseState", 2);
            }
        }
        if (mAttachment.size() != 0) {
            map.put("attachementuuid", uuid);
        }
        DialogHelp.showStatusLoading(false,mContext);
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).confirmAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(final AttendanceRecord attendanceRecord, final Response response) {
                HttpErrorCheck.checkCommitSus("确认打卡",response);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelp.cancelStatusLoading();
                        crolView.attendanceSuccess();
                    }
                },1000);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkCommitEro(error);
            }
        });
    }

    /**
     * 删除附件
     * */
    @Override
    public void deleteAttachments(String uuid,final Attachment delAttachment) {
        DialogHelp.showLoading(mActivity, "请稍后", true);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("bizType", 0);
        map.put("uuid", uuid);
        AttachmentService.remove(String.valueOf(delAttachment.getId()), map)
                .subscribe(new DefaultLoyoSubscriber<Attachment>(LoyoErrorChecker.COMMIT_DIALOG) {
                    @Override
                    public void onNext(Attachment attachment) {
                        DialogHelp.cancelStatusLoading();
                        crolView.deleteAttaSuccessEmbl(delAttachment);
                    }
                });
    }

    /**
     * 考勤数据验证
     * */
    @Override
    public boolean checkAttendanceData(String reason,String address,int outKind,int state) {
        if (TextUtils.isEmpty(reason)) {
            if (state == AttendanceRecord.STATE_OVERWORK) {
                if (outKind == 2) {
                    crolView.showMsg("加班原因不能为空");
                    return false;
                }
            } else if (state == AttendanceRecord.STATE_LEAVE_EARLY) {
                crolView.showMsg("早退原因不能为空");
                return false;
            } else if (state == AttendanceRecord.STATE_BE_LATE) {
                crolView.showMsg("迟到原因不能为空");
                return false;
            }
        }

        if (TextUtils.isEmpty(address)) {
            crolView.showMsg("地址不能为空");
            return false;
        }
        return true;
    }
}
