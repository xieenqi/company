package com.loyo.oa.v2.activityui.attendance.presenter;

import android.os.Handler;
import android.widget.TextView;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import java.util.ArrayList;

/**
 * Created by yyy on 16/10/10.
 */

public interface AttendanceAddPresenter {

    /*获取附件*/
    void getAttachments(String uuid);

    /*上传附件*/
    void uploadAttachments(String uuid,ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots);

    /*提交考勤*/
    void commitAttendance(ArrayList<Attachment> mAttachment,boolean isPopup, int outKind,int state,String uuid,String address,String reason,long extraWorkStartTime,long serverTime,int lateMin,int earlyMin);

    /*删除附件*/
    void deleteAttachments(String uuid, Attachment delAttachment);

    /*考勤数据验证*/
    boolean checkAttendanceData(String reason,String address,int outKind,int state);

    /*开始倒计时*/
    void countDown();

    /*计时器回收*/
    void recycle();

    /*刷新打卡位置*/
    void refreshLocation(final double longitude, final double latitude,String address);

    /*handler初始化*/
    Handler mHndler(TextView tvtime, TextView tvtime2, String tvTimeName);

}
