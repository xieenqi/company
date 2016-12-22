package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;
import com.loyo.oa.v2.beans.OrganizationalMember;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 工单详情 数据bean
 * Created by xeq on 16/8/30.
 */
public class WorksheetDetail implements Serializable{

    public String id;
    public String title;
    public String content;
    public String uuid;
    public OrganizationalMember dispatcher;
    public OrganizationalMember creator;
    public int triggerMode;//1 流程触发 2 定时触发
    public ArrayList<WorksheetEventsSupporter> sheetEventsSupporter;
    public WorksheetStatus status;

    public String auditorId;

    public long completedAt;
    public long confirmedAt;
    public long interruptAt;
    public long createdAt;
    public long updatedAt;
    public int totalNum;


    public int getTotalNum() {
        return sheetEventsSupporter.size();
    }

    public int getFinshedNum() {
        int count = 0;

        for (int i = 0; i < sheetEventsSupporter.size(); i++) {
            if (sheetEventsSupporter.get(i).status == WorksheetEventStatus.FINISHED) {
                count++;
            }
        }
        return count;
    }
}
