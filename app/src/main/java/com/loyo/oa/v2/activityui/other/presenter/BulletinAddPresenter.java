package com.loyo.oa.v2.activityui.other.presenter;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yyy on 16/10/9.
 */

public interface BulletinAddPresenter {

    /*通知人员回调处理*/
    void dealDepartmentResult(Members member);

    /*组装附件*/
    ArrayList<Attachment> assembleAttachment();

    /*格式验证*/
    void verifyText(String title,String content);

    /*提交通知公告*/
    void requestBulletinAdd(String title,String content,String uuid);

    /*上传附件*/
    void uploadAttachement(SweetAlertDialogView sweetAlertDialogView,ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots,String title,String content,String uuid);


}
