package com.loyo.oa.v2.activityui.attendance.viewcontrol;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/10.
 */

public interface AttendanceAddView {

    /*设置附件内容*/
    void setAttachmentEmbl(ArrayList<Attachment> mAttachment);

    /*删除附件成功*/
    void deleteAttaSuccessEmbl(Attachment mAttachment);

    /*提示信息*/
    void toastMessage(String message);

    /*打卡成功*/
    void attendanceSuccess();

    /*打卡超时*/
    void showTimeOutDialog();
}
