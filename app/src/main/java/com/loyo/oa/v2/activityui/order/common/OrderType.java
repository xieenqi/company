package com.loyo.oa.v2.activityui.order.common;

/**
 * 订单类型
 */

public enum OrderType {
    MY_ORDER(1), TEAM_ORDER(2);
    private int type;

    OrderType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
