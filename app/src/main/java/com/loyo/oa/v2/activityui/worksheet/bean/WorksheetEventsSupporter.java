package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.NewUser;

import java.io.Serializable;

/**
 * 工单信息 事件信息
 * Created by xeq on 16/8/30.
 */
public class WorksheetEventsSupporter implements Serializable {
    public String id;
    public String content;
    public long startTime;
    public long endTime;
    public int status;//1 待处理 2 未触发 3 已处理
    public NewUser responsor;
    public String responsorId;

}
