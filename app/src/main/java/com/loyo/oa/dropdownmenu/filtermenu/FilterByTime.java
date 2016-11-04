package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * Created by EthanGong on 2016/10/31.
 */

public enum FilterByTime {
    FOLLOW("跟进时间", "lastActAt"),
    CREATE("创建时间", "createdAt");

    public String key;
    public String value;
    FilterByTime(String value, String key) {
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
