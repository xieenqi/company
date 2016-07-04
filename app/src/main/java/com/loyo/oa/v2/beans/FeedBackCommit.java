package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.other.bean.User;

/**
 * Created by pj on 15/5/13.
 */
public class FeedBackCommit {

    int id;
    int feedbackId;
    String comment;
    User Creator;
    String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getCreator() {
        return Creator;
    }

    public void setCreator(User creator) {
        Creator = creator;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
