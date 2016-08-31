package com.loyo.oa.v2.activityui.worksheet.bean;

import java.io.Serializable;

/**
 * 事件详情 处理信息 item
 * Created by xeq on 16/8/31.
 */
public class EventHandleInfoItem implements Serializable {
    public int type;
    public String content;
    //    public int attachments;
    public String creatorId;
    public String creatorName;
    //    public int address;
    public long createdAt;
}
