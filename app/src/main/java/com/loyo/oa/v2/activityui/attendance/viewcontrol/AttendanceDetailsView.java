package com.loyo.oa.v2.activityui.attendance.viewcontrol;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceDetial;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/11.
 */

public interface AttendanceDetailsView {

    /*确认外勤加班成功处理*/
    void confirmOutEmbl();

    /*初始化附件*/
    void initGridView(ArrayList<Attachment> mAttachments);

    /*初始化详情数据*/
    void initDetails(HttpAttendanceDetial mAttendanceDetails);

    LoadingLayout getLoading();
}
