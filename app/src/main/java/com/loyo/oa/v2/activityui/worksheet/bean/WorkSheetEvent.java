package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.Groupable;
import com.loyo.oa.v2.activityui.worksheet.common.WorkSheetEventStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/8/27.
 */
public class WorkSheetEvent implements Groupable, Comparable<WorkSheetEvent>  {
    public String id;
    public String workSheetId;
    public int order;         // 顺序
    public String content;    // 事件描述
    public String responsorId;// 负责人ID
    public long startTime;
    public long endTime;
    public WorkSheetEventStatus status;

    @Override
    public WorkSheetEventStatus groupBy()
    {
        return status;
    }


    /** 相同状态下，按时间创建排序, 最新排前  */
    @Override
    public int compareTo(WorkSheetEvent another) {
        if (startTime < another.startTime) {
            return -1;
        }
        else if (startTime > another.startTime) {
            return 1;
        }
        return 0;
    }

    /** 测试数据 */
    public static WorkSheetEvent testInstance() {
        WorkSheetEvent wse = new WorkSheetEvent();
        wse.content = "内容内容内容";
        return wse;
    }


    public static List<WorkSheetEvent> getTestList() {
        ArrayList<WorkSheetEvent> result = new ArrayList<WorkSheetEvent>();

        for(int i = 0; i < 3; i ++) {
            WorkSheetEvent wse = testInstance();
            wse.status = WorkSheetEventStatus.WAITPROCESS;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorkSheetEvent wse = testInstance();
            wse.status = WorkSheetEventStatus.UNACTIVATED;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorkSheetEvent wse = testInstance();
            wse.status = WorkSheetEventStatus.FINISHED;
            result.add(wse);
        }

        for(int i = 0; i < 3; i ++) {
            WorkSheetEvent wse = testInstance();
            wse.status = WorkSheetEventStatus.TEMINATED;
            result.add(wse);
        }

        return result;
    }
}
