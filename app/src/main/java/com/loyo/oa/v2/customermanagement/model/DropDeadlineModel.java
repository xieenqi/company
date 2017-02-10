package com.loyo.oa.v2.customermanagement.model;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.customer.model.Customer;

/**
 * Created by EthanGong on 2017/2/10.
 */

public class DropDeadlineModel {
    public long activityRecycleAt;//跟进行为丢公海时间
    public long orderRecycleAt;   //订单丢公海时间

    public long saleactRecycleAt; // 跟进丢公海
    public long visitRecycleAt;   // 拜访丢公海
    public long voiceRecycleAt;   // 电话跟进丢公海
    public int conditionType;     // 条件类型： 1、全部（and）2、任意（or）

    public String getConditionString() {
        if (conditionType == 1) {
            return "全部";
        }
        else {
            return "任意";
        }
    }

    public static DropDeadlineModel getDeadlineModel(Customer customer) {


        DropDeadlineModel model = new DropDeadlineModel();

        if (customer != null) {
            model.activityRecycleAt = customer.activityRecycleAt;
            model.orderRecycleAt = customer.orderRecycleAt;
            model.saleactRecycleAt = customer.saleactRecycleAt;
            model.visitRecycleAt = customer.visitRecycleAt;
            model.voiceRecycleAt = customer.voiceRecycleAt;
            model.conditionType = customer.conditionType;
        }

        return model;
    }

    public static String formatDateTimeString(long timestamp) {
        return DateTool.getDateTimeReal(timestamp) + " 前";
    }
}
