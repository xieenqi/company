package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.model.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * com.loyo.oa.v2.beans
 * 描述 :通知  没个item详情
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
public class Bulletin extends BaseBeans {
    public String attachmentUUId;
    public ArrayList<Attachment> attachments = new ArrayList<>();
    public String content;
    public long createdAt;
    public String deptName;
    public String id;
    public boolean isPublic;
    public String title;
    public User creator;
    public ArrayList<BulletinViewer> viewers = new ArrayList<>();

    public String getUserName() {
        if (creator != null) {
            return creator.getRealname();
        }

        return "";
    }
    public String getPosition() {

        if (creator != null && creator.shortPosition != null) {
            return creator.shortPosition.getName();
        }

        return "";
    }

    public String getDeptName() {
        if (creator != null && creator.shortDept != null ) {
            return creator.shortDept.getName();
        }

        return "";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOrderStr() {
        return MainApp.getMainApp().df3.format(new Date(createdAt * 1000));
    }

    public ArrayList<String> attachmentUrls() {
        ArrayList<String> result = new ArrayList<String>();
        for (Attachment attachment:attachments) {
            final boolean isImage = (attachment.getAttachmentType() == Attachment.AttachmentType.IMAGE);

            if (isImage && attachment.getUrl() != null) {//预览图片
                result.add(attachment.getUrl());
            }
        }
        return result;
    }
}
