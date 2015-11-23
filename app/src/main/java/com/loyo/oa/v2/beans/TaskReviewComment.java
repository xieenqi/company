package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/24 0024.
 */
public class TaskReviewComment implements Serializable {
    private String   content ;//string, optional): ,
    private String  createdAt ;//&{time Time}, optional): ,
    private User   creator ;//&{organization User}, optional): ,
    private long   id ;//int64, optional): ,
    private String  mentionedDeptIds ;//string, optional): ,
    private String  mentionedGroupIds ;//string, optional): ,
    private String  mentionedUserIds ;//string, optional): ,
    private String   updatedAt ;//&{time Time}, optional):

    public TaskReviewComment() {

    }

    public TaskReviewComment(String _content) {
        content = _content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMentionedDeptIds() {
        return mentionedDeptIds;
    }

    public void setMentionedDeptIds(String mentionedDeptIds) {
        this.mentionedDeptIds = mentionedDeptIds;
    }

    public String getMentionedGroupIds() {
        return mentionedGroupIds;
    }

    public void setMentionedGroupIds(String mentionedGroupIds) {
        this.mentionedGroupIds = mentionedGroupIds;
    }

    public String getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(String mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
