package com.loyo.oa.v2.activityui.worksheet.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 工单 事件详情
 * Created by xeq on 16/8/31.
 */
public class EventDetail implements Serializable {
    public String id;
    public String wsId;
    public String title;
    public String templateId;
    public String templateName;
    public int triggerMode;//1 流程触发 2 定时触发
    public String content;
    public String responsorId;
    public String responsorName;
    public long startTime;
    public int daysDeadline;
    public int status;
    public long updatedAt;
    public ArrayList<EventHandleInfoItem> handleInfoList;

}
