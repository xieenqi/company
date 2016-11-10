package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Locate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 事件详情 处理信息 item
 * Created by xeq on 16/8/31.
 */
public class EventHandleInfoItem implements Serializable {
    public int type;//1 提交完成 2 打回重做
    public String content;
    public ArrayList<Attachment> attachments;
    public String creatorId;
    public String creatorName;
    public Locate address;
    public long createdAt;
}
