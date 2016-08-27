package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.worksheet.common.Groupable;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EthanGong on 16/8/27.
 */



public class Worksheet implements Groupable<WorksheetStatus>, Comparable<Worksheet> {


//    private static final Map<Integer /** groupKey */, Integer /** 排序权值weight */> sSortMap;
//    static
//    {
//        sSortMap = new HashMap<Integer, Integer>();
//        sSortMap.put(1, 0);
//        sSortMap.put(2, 1);
//    }

    public String id;
    public String companyId;
    public String orderId;      // 订单ID
    public String templateId;   // 模板ID
    public String title;        // 标题
    public String content;      // 内容
    public String UUID;         // 附件UUID
    public String responsorIds; // 负责人ID集合

    public String dispatcherId; // 分派人ID
    public int triggerMode;     // 触发模式 1,流程触发 2,定时触发
    public WorksheetStatus status;      // 工单状态 1,待分派 2,处理中 3 待审核 4 已完成 5 意外中止
    public long createdAt;      // 创建时间
    public long updatedAt;      // 更新时间
    public long confirmedAt;    // 确认时间
    public long completedAt;


    @Override
    public WorksheetStatus groupBy()
    {
        return status;
    }


    /** 相同状态下，按时间创建排序, 最新排前  */
    @Override
    public int compareTo(Worksheet another) {
        if (createdAt < another.createdAt) {
            return -1;
        }
        else if (createdAt > another.createdAt) {
            return 1;
        }
        return 0;
    }
}
