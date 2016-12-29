package com.loyo.oa.v2.activityui.work.bean;

import com.loyo.oa.v2.beans.NewUser;

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
    public int score;//点评分数 (启用 16-12-29)
    public String comment;//点评内容
    public String status;
    public String newScore;//新的点评分数

    public String id;
    public String name;
    public String avatar;

    public Reviewer() {

    }

    public Reviewer(NewUser user) {
        this.user = user;
    }
}
