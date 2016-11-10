package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum CommonSortType {

    CREATE("创建时间", "1"),
    UPDATE("最近更新", "2"),
    AMOUNT("最高金额", "3");

    public String key;
    public String value;
    CommonSortType(String value, String key) {
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
