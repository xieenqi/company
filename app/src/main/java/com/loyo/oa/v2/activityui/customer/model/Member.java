package com.loyo.oa.v2.activityui.customer.model;

import com.loyo.oa.v2.beans.OrganizationalMember;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户负责人
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class Member implements Serializable {
    private OrganizationalMember user;
    private boolean viewed;
    private long viewedAt;

    public long getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(long viewedAt) {
        this.viewedAt = viewedAt;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public OrganizationalMember getUser() {
        return user;
    }

    public void setUser(OrganizationalMember user) {
        this.user = user;
    }
}
