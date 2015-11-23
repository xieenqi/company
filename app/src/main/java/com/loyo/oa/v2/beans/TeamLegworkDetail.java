package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :团队拜访详细
 * 作者 : ykb
 * 时间 : 15/10/29.
 */
public class TeamLegworkDetail implements Serializable {
    private NewUser user;
    private int visitcount;

    public NewUser getUser() {
        return user;
    }

    public void setUser(NewUser user) {
        this.user = user;
    }

    public int getVisitCount() {
        return visitcount;
    }

    public void setVisitCount(int visitCount) {
        this.visitcount = visitCount;
    }
}
