package com.loyo.oa.v2.activityui.order.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum OrderStatus {
    ALL("全部状态", "0"),
    WAIT_APPROVE("待审核", "1"),
    APPROVE_PROCESSING("审核中", "7"),
    NOT_APPROVED("未通过", "2"),
    PROCESSING("进行中", "3"),
    FINISHED("已完成", "4"),
    TERMINATED("意外终止", "5");

    public String key;
    public String value;
    OrderStatus(String value, String key) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
}
