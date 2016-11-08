package com.loyo.oa.v2.activityui.wfinstance.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum SubmitStatus {
//    private static final String FILTER_STATUS[] = new String[]
//            {"全部状态", "待审批", "审批中", "未通过", "已通过"};

    ALL(0, "0", "全部状态"),
    WAIT_APPROVE(1, "1", "待审批"),
    APPROVING(2, "2", "审批中"),
    UNAPPROVED(3, "3", "未通过"),
    APPROVED(4, "4", "已通过");

    public int code;
    public String key;
    public String value;

    SubmitStatus(int code, String key, String value) {
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
