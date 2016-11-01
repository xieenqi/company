package com.loyo.oa.v2.activityui.work.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum WorkReportType {

//    private static final String FILTER_SEND_TYPE[] = new String[]{"全部类型", "提交给我的", "我提交的", "抄送给我的"};
//    private static final String FILTER_STATUS[] = new String[]{"全部状态", "待点评", "已点评"};
//    private static final String FILTER_TYPE[] = new String[]{"全部类别", "日报", "周报", "月报"};

    ALL(0, "0", "全部类型"),
    SUBMIT_TO_ME(1, "1", "提交给我的"),
    SUBMIT_BY_ME(2, "2", "我提交的"),
    CC_TO_ME(3, "3", "抄送给我的");

    public int code;
    public String key;
    public String value;

    WorkReportType(int code, String key, String value) {
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
