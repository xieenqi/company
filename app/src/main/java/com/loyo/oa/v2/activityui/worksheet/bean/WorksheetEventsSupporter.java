package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.beans.OrganizationalMember;

import java.io.Serializable;

/**
 * 工单信息 事件信息
 * Created by xeq on 16/8/30.
 */
public class WorksheetEventsSupporter implements Serializable {
    public String id;
    public String content;

    public WorksheetEventStatus status;//1 待处理 2 未触发 3 已处理
    public OrganizationalMember responsor;
    public String responsorId;
    public boolean isOvertime;//事件截至时间是否超时  true 表示超时

    public int triggerMode;


    public long createdAt;
    public long updatedAt;
    public long startTime;
    public long endTime;
    public long finishTime;

    public int daysDeadline;
    public int daysLater;
}
