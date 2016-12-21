package com.loyo.oa.v2.activityui.customer.common;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.tool.Utils;

/**
 * Created by xeq on 16/12/16.
 */

public class CommonMethod {

    public static void commonCustomerRecycleTime(Customer customer, ViewGroup layout, TextView tView1, TextView tView2) {
        if (customer.activityRecycleAt != 0 || customer.orderRecycleAt != 0) {
            layout.setVisibility(View.VISIBLE);
            if (customer.activityRecycleAt != 0) {
                tView1.setVisibility(View.VISIBLE);
                String tt1 = "无跟进丢公海：";
                String time = DateTool.getDateTimeFriendly(customer.activityRecycleAt);
                tView1.setText(customer.activityRemind ?
                        Utils.modifyTextColor(tt1 + time, Color.parseColor("#f5625a"), tt1.length(), (tt1 + time).length())
                        : tt1 + time);
            } else {
                tView1.setVisibility(View.GONE);
            }
            if (customer.orderRecycleAt != 0) {
                tView2.setVisibility(View.VISIBLE);
                String tt2 = "无订单丢公海：";
                String time = DateTool.getDateTimeFriendly(customer.orderRecycleAt);
                tView2.setText(customer.orderRemind ?
                        Utils.modifyTextColor(tt2 + time, Color.parseColor("#f5625a"), tt2.length(), (tt2 + time).length())
                        : tt2 + time);
            } else {
                tView2.setVisibility(View.GONE);
            }
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private static boolean isOutCurrentTime(long time) {
        if (System.currentTimeMillis() / 1000 > time) {
            return true;
        }
        return false;
    }
}
