package com.loyo.oa.v2.activityui.tasks.bean;

import com.loyo.oa.v2.activityui.other.bean.User;

import java.io.Serializable;

public class TaskTpl implements Serializable {
    String id;
    String title;
    String content;
    User responsiblePerson;
    String joinedUserIds;
    boolean reviewFlag;
    String createdAt;
    String updatedAt;

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

    public User getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(User responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getJoinedUserIds() {
        return joinedUserIds;
    }

    public void setJoinedUserIds(String joinedUserIds) {
        this.joinedUserIds = joinedUserIds;
    }

    public boolean isReviewFlag() {
        return reviewFlag;
    }

    public void setReviewFlag(boolean reviewFlag) {
        this.reviewFlag = reviewFlag;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
