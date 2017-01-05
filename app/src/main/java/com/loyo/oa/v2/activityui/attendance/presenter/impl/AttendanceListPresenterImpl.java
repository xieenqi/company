package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;

import com.loyo.oa.common.utils.DateFormatSet;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.attendance.api.AttendanceService;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceListPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.HashMap;

/**
 * 【考勤列表】
 * Created by yyy on 16/10/11.
 */

public class AttendanceListPresenterImpl implements AttendanceListPresenter {

    public Activity mActivity;
    public AttendanceListView crolView;

    public AttendanceListPresenterImpl(Activity mActivity, AttendanceListView crolView){
        this.mActivity = mActivity;
        this.crolView  = crolView;

    }

    /**
     * 获取列表信息
     * */
    @Override
    public void getListData(final int type, int qtime, final int page) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        map.put("qtime", qtime);
        map.put("pageIndex", page);
        map.put("pageSize", 2000);

        AttendanceService.getAttendances(map)
                .subscribe(new DefaultLoyoSubscriber<HttpAttendanceList>(crolView.getLoading()) {
            @Override
            public void onNext(HttpAttendanceList mAttendanceList) {
                crolView.getListDataEmbl(mAttendanceList);
            }
        });
    }

    /**
     * 获取团队统计信息(迟到,早退...)
     * */
    @Override
    public void getTeamData(int qtime) {
        LoyoProgressHUD hud = crolView.showProgress("");
        AttendanceService.getTeamCount(qtime).subscribe(new DefaultLoyoSubscriber<AttendanceList>(hud) {
            @Override
            public void onNext(AttendanceList mAttendanceList) {
                crolView.getTeamDataEmbl(mAttendanceList);

            }
        });
    }



    /**
     * 打卡信息验证
     * */
    @Override
    public void getValidateInfo() {
        LoyoProgressHUD hud = crolView.showProgress("");
        AttendanceService.validateAttendance().subscribe(new DefaultLoyoSubscriber<ValidateInfo>(hud) {
            @Override
            public void onNext(ValidateInfo validateInfo) {
                crolView.getValidateInfoEmbl(validateInfo);
            }
        });
    }

    /**
     * 打卡信息检查
     * */
    @Override
    public void checkAttendance(HashMap<String,Object> map, final String address) {
        LoyoProgressHUD hud = crolView.showProgress("");
        AttendanceService.checkAttendance(map).subscribe(new DefaultLoyoSubscriber<AttendanceRecord>(hud) {
            @Override
            public void onNext(AttendanceRecord mRecord) {
                crolView.checkAttendanceEmbl(mRecord,address);
            }
        });
    }

    private int getDateTime(long qtime) {
        return Integer.valueOf(DateFormatSet.dateNumSdf.format(qtime*1000));
    }
}
