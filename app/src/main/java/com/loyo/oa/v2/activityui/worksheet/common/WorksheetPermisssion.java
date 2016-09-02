package com.loyo.oa.v2.activityui.worksheet.common;

/**
 * Created by EthanGong on 16/9/2.
 */

import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.tool.GsonUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  工单状态|    角色     |   事件状态     |   事件操作
 * ---------------------------------------------------------------------------------
 *         |  分派人       only-未触发        转移（有负责人时），分派（无负责人时） ｜ 二选一
 * 待分派   |------------------------------------------------------------------------
 *         |  负责人       only－未触发        none
 * ---------------------------------------------------------------------------------
 *         |  分派人         未触发            转移
 *         |                待处理            转移
 *         |                已处理            重做
 *  进行中  | -----------------------------------------------------------------------
 *         |  负责人         未触发            none
 *         |                待处理            完成
 *         |                已处理            none
 *  --------------------------------------------------------------------------------
 *         |  分派人        only-已处理        重做
 *  待审核  |------------------------------------------------------------------------
 *         |  负责人        only-已处理        none
 * ---------------------------------------------------------------------------------
 *  已完成  |  分派人&负责人     已处理           none
 * ---------------------------------------------------------------------------------
 *  意外终止|  分派人&负责人      all            none
 *
 * */

public class WorksheetPermisssion {

    class Permisssions {
        ArrayList<WorksheetPermisssion> permisssions;
    }

    private final static String json =
            "{permisssions:\n" +
                    "[\n" +
                    "{status:1, role:4, eventStatus:2, hasResponsor:true, action:1},\n" +
                    "{status:1, role:4, eventStatus:2, hasResponsor:false, action:2},\n" +
                    "\n" +
                    "{status:1, role:2, eventStatus:2, hasResponsor:true, action:0},\n" +
                    "{status:1, role:2, eventStatus:2, hasResponsor:false, action:0},\n" +
                    "\n" +
                    "{status:2, role:4, eventStatus:2, hasResponsor:true, action:1},\n" +
                    "{status:2, role:4, eventStatus:1, hasResponsor:true, action:1},\n" +
                    "{status:2, role:4, eventStatus:3, hasResponsor:true, action:3},\n" +
                    "{status:2, role:4, eventStatus:2, hasResponsor:false, action:1},\n" +
                    "{status:2, role:4, eventStatus:1, hasResponsor:false, action:1},\n" +
                    "{status:2, role:4, eventStatus:3, hasResponsor:false, action:3},\n" +
                    "\n" +
                    "{status:2, role:2, eventStatus:2, hasResponsor:true, action:0},\n" +
                    "{status:2, role:2, eventStatus:1, hasResponsor:true, action:4},\n" +
                    "{status:2, role:2, eventStatus:3, hasResponsor:true, action:0},\n" +
                    "{status:2, role:2, eventStatus:2, hasResponsor:false, action:0},\n" +
                    "{status:2, role:2, eventStatus:1, hasResponsor:false, action:4},\n" +
                    "{status:2, role:2, eventStatus:3, hasResponsor:false, action:0},\n" +
                    "\n" +
                    "\n" +
                    "{status:3, role:4, eventStatus:3, hasResponsor:true, action:3},\n" +
                    "{status:3, role:4, eventStatus:3, hasResponsor:false, action:3}\n" +
                    "]\n" +
                    "}";

    private static ArrayList<WorksheetPermisssion> permisssions;
    static {
        permisssions = GsonUtils.newInstance().fromJson(json, Permisssions.class).permisssions;
    }

    private WorksheetStatus status;
    private WSRole role;
    private WorksheetEventStatus eventStatus;
    private boolean hasResponsor;
    private WorksheetEventAction action;


    public static ArrayList<WorksheetEventAction>
    actionsFor(WorksheetStatus status, WSRole role, WorksheetEventStatus eventStatus, boolean hasResponsor)
    {

        ArrayList<WorksheetEventAction> result = new ArrayList<WorksheetEventAction>();
        if (permisssions == null || role == null) {
            return result;
        }

        Iterator<WorksheetPermisssion> iterator = permisssions.iterator();
        while (iterator.hasNext()) {
            WorksheetPermisssion p = iterator.next();
            if (p.status == status
                    && role.hasRole(p.role)
                    && p.eventStatus == eventStatus
                    && p.hasResponsor == hasResponsor)
            {
                result.add(p.action);
            }
        }

        return result;
    }


    /* 测试 */

    public static void test() {
        ArrayList<WorksheetPermisssion> permisssions = WorksheetPermisssion.permisssions;
    }
}
