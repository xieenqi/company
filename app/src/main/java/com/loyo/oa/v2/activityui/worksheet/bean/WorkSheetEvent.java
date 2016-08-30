package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.Groupable;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/8/27.
 */

public class WorksheetEvent implements Groupable, Comparable<WorksheetEvent>  {
    public String id;
    public String workSheetId;
    public int order;         // 顺序
    public String content;    // 事件描述
    public String responsorId;// 负责人ID
    public long startTime;
    public long endTime;
    public WorksheetEventStatus status;

    @Override
    public WorksheetEventStatus groupBy()
    {
        return status;
    }


    /** 相同状态下，按时间创建排序, 最新排前  */
    @Override
    public int compareTo(WorksheetEvent another) {
        if (startTime < another.startTime) {
            return -1;
        }
        else if (startTime > another.startTime) {
            return 1;
        }
        return 0;
    }

    /** 测试数据 */
    public static WorksheetEvent testInstance() {
        WorksheetEvent wse = new WorksheetEvent();
        wse.content = "内容内容内容";
        return wse;
    }


    public static List<WorksheetEvent> getTestList() {
        ArrayList<WorksheetEvent> result = new ArrayList<WorksheetEvent>();

        for(int i = 0; i < 3; i ++) {
            WorksheetEvent wse = testInstance();
            wse.status = WorksheetEventStatus.WAITPROCESS;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorksheetEvent wse = testInstance();
            wse.status = WorksheetEventStatus.UNACTIVATED;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorksheetEvent wse = testInstance();
            wse.status = WorksheetEventStatus.FINISHED;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorksheetEvent wse = testInstance();
            wse.status = WorksheetEventStatus.TEMINATED;
            result.add(wse);
        }

        return result;
    }
}