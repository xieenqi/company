package com.loyo.oa.v2.activityui.attendance.viewcontrol;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/10.
 */

public interface AttendanceAddView extends BaseView{

    /*设置附件内容*/
    void setAttachmentEmbl(ArrayList<Attachment> mAttachment);

    /*删除附件成功*/
    void deleteAttaSuccessEmbl(Attachment mAttachment);

    /*打卡成功*/
    void attendanceSuccess();

    /*打卡超时*/
    void showTimeOutDialog();
}
