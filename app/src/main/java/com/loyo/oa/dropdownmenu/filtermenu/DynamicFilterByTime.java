package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * 【跟进和拜访】菜单时间参数
 * Created by yyy on 2016/10/31.
 */

public enum DynamicFilterByTime {

    UNLIMITED("不限","one"),
    TODAY("今天","one"),
    YESTERDAY("昨天","one"),
    TOWEEK("本周","one"),
    LASTWEEK("上周","one"),
    TOMONTH("本月","one"),
    LASTMONTH("上月","one");

    public String key;
    public String value;
    DynamicFilterByTime(String value, String key) {
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
