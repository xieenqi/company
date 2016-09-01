package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.tool.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 16/8/30.
 */


public class WorksheetOrder implements Serializable {

    public String id;
    public String title;
    public String proName;
    public String dealMoney;
    public String customerName;

    private static WorksheetOrder instance() {
        WorksheetOrder ws = new WorksheetOrder();
        ws.proName = "产品";
        ws.title = "订单订单";
        ws.id = StringUtil.getUUID();
        ws.dealMoney = "1000";
        ws.customerName = "金牌客户";
        return ws;
    }

    public static WorksheetOrder converFromDetail(OrderDetail detail) {
        WorksheetOrder ws = new WorksheetOrder();
        ws.proName = detail.proName;
        ws.title = detail.title;
        ws.id = detail.id;
        ws.dealMoney = String.valueOf(detail.dealMoney);
        ws.customerName = detail.customerName;
        return ws;
    }

    public static List<WorksheetOrder> testData() {
        List<WorksheetOrder> result = new ArrayList<WorksheetOrder>();

        for (int i = 0; i < 10; i++) {
            result.add(WorksheetOrder.instance());
        }

        return result;
    }
}
