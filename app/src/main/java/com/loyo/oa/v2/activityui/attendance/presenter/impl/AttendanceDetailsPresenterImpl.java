package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;
import android.text.TextUtils;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.api.AttendanceService;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceDetailsPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceDetailsView;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.ArrayList;

/**
 * 【考勤详情】Presenter
 * Created by yyy on 16/10/11.
 */

public class AttendanceDetailsPresenterImpl implements AttendanceDetailsPresenter {

    public AttendanceDetailsView crolView;
    public Activity mActivity;

    public AttendanceDetailsPresenterImpl(AttendanceDetailsView crolView, Activity mActivity){
        this.crolView = crolView;
        this.mActivity = mActivity;
    }

    /**
     * 获取附件
     * */
    @Override
    public void getAttachments(String attachementuuid) {
        if (TextUtils.isEmpty(attachementuuid)) {//附件id为空
            return;
        }

        AttachmentService.getAttachments(attachementuuid)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>() {
                    @Override
                    public void onNext(ArrayList<Attachment> attachments) {
                        crolView.initGridView(attachments);
                    }
                });
    }

    /**
     * 获取考勤详情
     * */
    @Override
    public void getData(String attendanceId) {
        AttendanceService.getAttendancesDetial(attendanceId)
                .subscribe(new DefaultLoyoSubscriber<HttpAttendanceDetial>(crolView.getLoading()) {
            @Override
            public void onNext(HttpAttendanceDetial mDetails) {
                crolView.initDetails(mDetails);
            }
        });
    }

    /**
     * 确认加班或外勤
     * */
    @Override
    public void confirmOutAttendance(String attendanceId,int type) {
        LoyoProgressHUD hud = crolView.showProgress("");
        AttendanceService.confirmOutAttendance(attendanceId,type)
                .subscribe(new DefaultLoyoSubscriber<AttendanceRecord>(hud) {
            @Override
            public void onNext(AttendanceRecord attendanceRecord) {
                crolView.confirmOutEmbl();
            }
        });
    }
}
