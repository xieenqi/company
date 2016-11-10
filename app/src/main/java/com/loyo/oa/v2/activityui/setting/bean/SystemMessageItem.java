package com.loyo.oa.v2.activityui.setting.bean;

import com.loyo.oa.v2.activityui.other.model.User;

import java.io.Serializable;

/**
 * Created by xeq on 16/11/7.
 */

public class SystemMessageItem implements Serializable {

    public String id;
    public String bizzId;
    public String title;
    public User creator;
    public long createdAt;
    public long viewedAt;
    public SystemMessageItemType bizzType;
}
