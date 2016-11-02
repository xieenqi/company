package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * Created by EthanGong on 2016/10/31.
 */

public enum SortOrder {
    DESC("倒序", "desc"),
    ASC("顺序", "asc");

    public String key;
    public String value;
    SortOrder(String value, String key) {
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
