package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.application.MainApp;

import java.util.ArrayList;
import java.util.Date;

/**
 * com.loyo.oa.v2.beans
 * 描述 :通知
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
public class Bulletin extends BaseBeans {
    private String attachmentUUId;
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String content;
    private long createdAt;
//    private String deptName;
    private String id;
    private boolean isPublic;
    private User creator;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    private String title;
    private ArrayList<BulletinViewer> viewers = new ArrayList<>();

    public ArrayList<BulletinViewer> getViewers() {
        return viewers;
    }

    public void setViewers(ArrayList<BulletinViewer> viewers) {
        this.viewers = viewers;
    }

    public String getUserName() {
        if (creator != null) {
            return creator.getRealname();
        }

        return "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        if (creator != null && creator.getShortPosition() != null) {
            return creator.getShortPosition().getName();
        }

        return "";
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptName() {
        if (creator != null && creator.getShortDept() != null ) {
//            return creator.getDepts().get(0).getName();
            return creator.getShortDept().getName();
        }

        return "";
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    String getOrderStr() {
        return MainApp.getMainApp().df3.format(new Date(createdAt*1000));
    }
}
