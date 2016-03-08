package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/10/12.
 */
public class UserInfo implements Serializable {


    private String title;
    private Department shortDept;
    private Position shortPosition;

    public Position getShortPosition() {
        return shortPosition;
    }

    public void setShortPosition(Position shortPosition) {
        this.shortPosition = shortPosition;
    }

    public Department getShortDept() {
        return shortDept;
    }

    public void setShortDept(Department shortDept) {
        this.shortDept = shortDept;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
