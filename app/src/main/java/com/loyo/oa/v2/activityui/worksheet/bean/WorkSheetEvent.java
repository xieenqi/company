package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;

/**
 * Created by EthanGong on 16/8/27.
 */
public class WorkSheetEvent {
    public String id;
    public String workSheetId;
    public int order;         // 顺序
    public String content;    // 事件描述
    public String responsorId;// 负责人ID
    public long startTime;
    public long endTime;
    public WorksheetEventStatus status;
}
