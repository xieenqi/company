package com.loyo.oa.v2.ui.activity.work.bean;

import com.loyo.oa.v2.ui.activity.other.bean.User;

import java.io.Serializable;

public class WorkReportTpl implements Serializable {
    String id;
    String title;
    String content;
    User reviewer;
    String mentionedUserIds;
    String mentionedDeptIds;
    int reportType;
    String createdAt;
    String updatedAt;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public String getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(String mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public String getMentionedDeptIds() {
        return mentionedDeptIds;
    }

    public void setMentionedDeptIds(String mentionedDeptIds) {
        this.mentionedDeptIds = mentionedDeptIds;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
