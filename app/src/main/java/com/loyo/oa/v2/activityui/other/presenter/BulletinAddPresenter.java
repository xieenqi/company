package com.loyo.oa.v2.activityui.other.presenter;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.beans.Members;

import java.util.ArrayList;

/**
 * Created by yyy on 16/10/9.
 */

public interface BulletinAddPresenter {

    /* 获取Members */
    Members getMembers();

    /*通知人员回调处理*/
    void dealDepartmentResult(Members member);

    /*组装附件*/
    ArrayList<Attachment> assembleAttachment(ArrayList<AttachmentForNew> attachments);

    /*格式验证*/
    void verifyText(String title,String content);

    /*提交通知公告*/
    void requestBulletinAdd(String title,String content,String uuid, ArrayList<AttachmentForNew> attachments);

    /*上传附件*/
    //void uploadAttachement(SweetAlertDialogView sweetAlertDialogView, ArrayList<ImageInfo> pickPhots, String title, String content, String uuid);


}