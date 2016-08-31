package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.NewUser;

import java.util.ArrayList;

/**
 * 工单详情 数据bean
 * Created by xeq on 16/8/30.
 */
public class WorksheetDetial {

    public String id;
    public String title;
    public String content;
    public String uuid;
    public NewUser dispatcher;
    public NewUser creator;
    public int triggerMode;//1 流程触发 2 定时触发
    //    public String dispatcherId;//分派人id
//    public String creatorId;
    //    public String id;
//    public String id;
    public ArrayList<WorksheetEventsSupporter> sheetEventsSupporter;
    public int status;//1.待分派  2.进行中 3.待审核 4.已完成 5.意外终止
}
