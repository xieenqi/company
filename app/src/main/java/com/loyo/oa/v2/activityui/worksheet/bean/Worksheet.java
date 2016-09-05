package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.common.Groupable;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/8/27.
 */

//{
//        "id": "57c3e4f735d8605cbc229b08",
//        "companyId": "5784cde6ebe07f834f000001",
//        "orderId": "57c002e5526f154631000004",
//        "templateId": "57c00ad7b0207a0380000001",
//        "orderName": "新建的工单",
//        "title": "工单事件很多哟!",
//        "content": "123",
//        "uuid": "abbc81b3-f674-4c48-b6e3-1b2a3e103ba9",
//        "dispatcherId": "5784ceb8526f1566b05e331f",
//        "dispatcherName": "孙文",
//        "dispatcherAt": "",
//        "creatorId": "57859acd526f1566b05e332a",
//        "creatorName": "朱浩",
//        "triggerMode": 1,
//        "status": 1,
//        "createdAt": 1472455927,
//        "updatedAt": 1472455927,
//        "confirmedAt": 0,
//        "completedAt": 0,
//        "totalCount": 1,
//        "finishCount": 0
//}

public class Worksheet implements Groupable, Comparable<Worksheet>, Serializable {

    public String id;
    public String companyId;
    public String orderId;      // 订单ID
    public String orderName;    // 订单
    public String templateId;   // 模板ID
    public String title;        // 标题
    public String content;      // 内容
    public String uuid;         // 附件UUID
    public String responsorIds; // 负责人ID集合

    public String dispatcherId;   // 分派人ID
    public String dispatcherName; // 分派人姓名
    public String dispatcherAt;   // 分派时间
    public String creatorId;      // 创建人ID
    public String creatorName;    // 创建人姓名
    public int triggerMode;       // 触发模式 1,流程触发 2,定时触发
    public WorksheetStatus status;// 工单状态 1,待分派 2,处理中 3 待审核 4 已完成 5 意外中止
    public long createdAt;        // 创建时间
    public long updatedAt;        // 更新时间
    public long confirmedAt;      // 确认时间
    public long completedAt;
    public int totalCount;
    public int finishCount;

    public ArrayList<WorksheetEvent> workSheetEvents;


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


    /** 测试数据 */
    public static Worksheet testInstance() {
        Worksheet ws = new Worksheet();
        ws.content = "内容内容内容";
        return ws;
    }


    public static List<Worksheet> getTestList() {
        ArrayList<Worksheet> result = new ArrayList<Worksheet>();

        for(int i = 0; i < 3; i ++) {
            Worksheet ws = testInstance();
            ws.status = WorksheetStatus.WAITASSIGN;
            result.add(ws);
        }

        for(int i = 0; i < 3; i ++) {
            Worksheet ws = testInstance();
            ws.status = WorksheetStatus.INPROGRESS;
            result.add(ws);
        }

        for(int i = 0; i < 3; i ++) {
            Worksheet ws = testInstance();
            ws.status = WorksheetStatus.WAITAPPROVE;
            result.add(ws);
        }

        for(int i = 0; i < 3; i ++) {
            Worksheet ws = testInstance();
            ws.status = WorksheetStatus.FINISHED;
            result.add(ws);
        }

        for(int i = 0; i < 3; i ++) {
            Worksheet ws = testInstance();
            ws.status = WorksheetStatus.TEMINATED;
            result.add(ws);
        }

        return result;
    }

}
