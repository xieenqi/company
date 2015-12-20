package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :报告点评人
 * 作者 : ykb
 * 时间 : 15/10/12.
 */
public class Reviewer implements Serializable {
    private NewUser user;
    private boolean viewed;//k看过吗
    private boolean reviewed;//点评过吗
    private long viewAt;//看的时间
    private long reviewAt;//点评时间
    private int score;//点评分数
    private String comment;//点评内容

    public String id;
    public String name;
    public String avatar;

    public Reviewer(NewUser user) {
        this.user = user;
    }


    public String getComment() {
        return comment;
    }

    public int getScore() {
        return score;
    }


    public boolean isReviewed() {
        return reviewed;
    }


    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public NewUser getUser() {
        return user;
    }

    public void setUser(NewUser user) {
        this.user = user;
    }
}
