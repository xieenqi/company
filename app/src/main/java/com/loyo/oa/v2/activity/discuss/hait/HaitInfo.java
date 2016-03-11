package com.loyo.oa.v2.activity.discuss.hait;

import java.io.Serializable;

/**
 * Created by loyocloud on 16/3/10.
 */
public class HaitInfo implements Serializable {
    private String time = "2016/03/10";
    private String title = "call to OpenGL ES API with no current context (logged once per thread)";
    private String name = "刘德";
    private String group = "公司年会活动";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
