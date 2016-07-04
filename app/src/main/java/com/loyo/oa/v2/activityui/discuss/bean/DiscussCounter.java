package com.loyo.oa.v2.activityui.discuss.bean;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :讨论信息
 * 作者 : ykb
 * 时间 : 15/10/16.
 */
public class DiscussCounter implements Serializable {

    private int total;
    private boolean viewed;

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
