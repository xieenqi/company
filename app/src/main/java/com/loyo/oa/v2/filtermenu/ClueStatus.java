package com.loyo.oa.v2.filtermenu;

/**
 * Created by EthanGong on 2016/10/31.
 */

public enum ClueStatus {
    All(0, "0", "全部"),
    NOT_PROCESS(1, "1", "未处理"),
    ALREADY_CONTACT(2, "2", "已联系"),
    CLOSED(3, "3", "关闭");

    public int code;
    public String key;
    public String value;

    ClueStatus(int code, String key, String value) {
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
