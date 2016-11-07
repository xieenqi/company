package com.loyo.oa.v2.activityui.wfinstance.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum  ApproveStatus {
//    private static final String FILTER_STATUS[] = new String[]
// {"全部状态", "待我审批的", "未到我审批的", "我同意的", "我驳回的"};

    ALL(0, "0", "不限状态"),
    WAIT_APPROVE(1, "1", "待我审批的"),
    APPROVED_BY_ME(2, "2", "我同意的"),
    REJECTED_BY_ME(3, "3", "我驳回的"),
    BEFORE_ME(4, "4", "未到我审批的");

    public int code;
    public String key;
    public String value;

    ApproveStatus(int code, String key, String value) {
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
