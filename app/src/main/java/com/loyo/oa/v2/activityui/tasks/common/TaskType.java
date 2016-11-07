package com.loyo.oa.v2.activityui.tasks.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum TaskType {
//    private static final String[] TYPE_TAG = new String[]{"全部类型", "我分派的", "我负责的", "我参与的"};
//    private static final String[] STATUS_TAG = new String[]{"全部状态", "未完成", "待审核", "已完成"};

    ALL(0, "0", "不限类型"),
    ASSIGN_BY_ME(1, "1", "我分派的"),
    IN_CHARGE(2, "2", "我负责的"),
    PARTICIPATE_IN(3, "3", "我参与的");

    public int code;
    public String key;
    public String value;

    TaskType(int code, String key, String value) {
        this.code = code;
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
