package com.loyo.oa.v2.activityui.tasks.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum TaskStatus {

//    private static final String[] TYPE_TAG = new String[]{"全部类型", "我分派的", "我负责的", "我参与的"};
//    private static final String[] STATUS_TAG = new String[]{"全部状态", "未完成", "待审核", "已完成"};

    ALL(0, "0", "不限状态"),
    NOT_FINISHED(1, "1", "未完成"),
    WAIT_APPROVE(2, "2", "待审核"),
    FINISHED(3, "3", "已完成");

    public int code;
    public String key;
    public String value;

    TaskStatus(int code, String key, String value) {
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
