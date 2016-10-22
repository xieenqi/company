package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.BaseBean;
import java.util.ArrayList;

/**
 * 【工单信息】
 * Created by yyy on 16/8/31.
 */
public class WorksheetInfo extends BaseBean{

    public String id;
    public String companyId;
    public String orderId;
    public String templateName;
    public String orderName;
    public String title;
    public String content;
    public String uuid;
    public String dispatcherId;
    public String dispatcherName;
    public String responsorNames;

    public ArrayList<Attachment> attachment = new ArrayList<>();
    public String creatorId;
    public String creatorName;
    public int    triggerMode;
    public int    status;
    public long   createdAt;
    public long   updatedAt;
    public long   completedAt; //完成时间
    public long   confirmedAt; //确认时间, 多个事件确定负责人的时间，对应界面上分派时间（区别于dispatcherAt）
    public long   interruptAt; //终止时间

}
