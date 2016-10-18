package com.loyo.oa.v2.activityui.attendance.presenter.impl;

import android.app.Activity;
import android.text.TextUtils;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;
import com.loyo.oa.v2.activityui.attendance.presenter.AttendanceDetailsPresenter;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceDetailsView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import java.util.ArrayList;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

        Utils.getAttachments(attachementuuid, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(final ArrayList<Attachment> mAttachment, final Response response) {
                HttpErrorCheck.checkResponse("考勤详情-获取附件", response);
                crolView.initGridView(mAttachment);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 获取考勤详情
     * */
    @Override
    public void getData(String attendanceId) {
        DialogHelp.showLoading(mActivity, "请稍后", true);
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).getAttendancesDetial(attendanceId, new RCallback<HttpAttendanceDetial>() {
            @Override
            public void success(final HttpAttendanceDetial mDetails, final Response response) {
                HttpErrorCheck.checkResponse("考勤详情-->", response);
                crolView.initDetails(mDetails);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 确认加班或外勤
     * */
    @Override
    public void confirmOutAttendance(String attendanceId,int type) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class).
                confirmOutAttendance(attendanceId, type, new RCallback<AttendanceRecord>() {
                    @Override
                    public void success(final AttendanceRecord record, final Response response) {
                        HttpErrorCheck.checkResponse(" 考勤返回 ", response);
                        crolView.confirmOutEmbl();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });
    }
}
