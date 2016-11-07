package com.loyo.oa.v2.activityui.project.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum ProjectStatus {

//    private static final String[] FILTER_STATUS_ARRAY = new String[]{"全部状态", "进行中", "已结束"};

    ALL(0, "0", "不限状态"),
    IN_PROCESS(1, "1", "进行中"),
    TERMINATED(2, "2", "已结束");

    public int code;
    public String key;
    public String value;

    ProjectStatus(int code, String key, String value) {
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
