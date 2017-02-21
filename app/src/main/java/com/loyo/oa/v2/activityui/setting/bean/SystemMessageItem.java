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
    public int jumpType;// 客户： 0，到详情 1，到列表
    public int messageType;//区分 普通客户消息和公海客户消息，当bizzType=6&&messageType==1为公海客户提醒消息。

}
