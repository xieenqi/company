package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * Created by pj on 15/5/13.
 */
public class Feedback {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ArrayList<FeedBackCommit> getComments() {
        if (comments == null) {
            return new ArrayList<>();
        }
        return comments;
    }

    public void setComments(ArrayList<FeedBackCommit> comments) {
        this.comments = comments;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    int id;
    int type;
    String content;
    User creator;
    String companyName;
    ArrayList<FeedBackCommit> comments;
    String created_at;
}
