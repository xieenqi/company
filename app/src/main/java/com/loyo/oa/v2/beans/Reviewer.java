package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :报告点评人
 * 作者 : ykb
 * 时间 : 15/10/12.
 */
public class Reviewer implements Serializable {

    public NewUser user = new NewUser();
    public boolean viewed;//k看过吗
    public boolean reviewed;//点评过吗
    public long reviewedAt;//点评时间
    public int score;//点评分数
    public String comment;//点评内容
    public String status;

    public String id;
    public String name;
    public String avatar;

    public Reviewer() {

    }

    public Reviewer(NewUser user) {
        this.user = user;
    }
//
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//
//    public String getComment() {
//        return comment;
//    }
//
//    public int getScore() {
//        return score;
//    }
//
//    public long getReviewedAt() {
//        return reviewedAt;
//    }
//
//    public void setReviewedAt(long reviewedAt) {
//        this.reviewedAt = reviewedAt;
//    }
//
//
//    public boolean isReviewed() {
//        return reviewed;
//    }
//
//
//    public boolean isViewed() {
//        return viewed;
//    }
//
//    public void setViewed(boolean viewed) {
//        this.viewed = viewed;
//    }
//
//    public NewUser getUser() {
//        return user;
//    }
//
//    public void setUser(NewUser user) {
//        this.user = user;
//    }
}
