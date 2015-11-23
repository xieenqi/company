package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Discussion implements Serializable, Comparable {

    private int bizId;
    private int bizType;
    private String content;
    private User creator;
    private long createdAt;
    private String attachmentUUId;// (string, optional): ,
    private ArrayList<Attachment> attachments = new ArrayList<>();
    private String id;
    private String mentionedDeptIds;
    private String mentionedGroupIds;
    private String mentionedUserIds;

    public String getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(String mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public String getMentionedGroupIds() {
        return mentionedGroupIds;
    }

    public void setMentionedGroupIds(String mentionedGroupIds) {
        this.mentionedGroupIds = mentionedGroupIds;
    }

    public String getMentionedDeptIds() {
        return mentionedDeptIds;
    }

    public void setMentionedDeptIds(String mentionedDeptIds) {
        this.mentionedDeptIds = mentionedDeptIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getBizId() {
        return bizId;
    }

    public void setBizId(int bizId) {
        this.bizId = bizId;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof Discussion)) {
            return 1;
        }

        Discussion d = (Discussion) o;

        Date thisDate = new Date(getCreatedAt() * 1000);
        Date dDate = new Date(d.getCreatedAt() * 1000);

        return thisDate.after(dDate) ? 1 : 0;
    }
}
