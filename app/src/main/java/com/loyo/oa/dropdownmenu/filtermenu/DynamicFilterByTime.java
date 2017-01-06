package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * 【跟进和拜访】菜单时间参数
 * Created by yyy on 2016/10/31.
 */

public enum DynamicFilterByTime {


    //根据后台规则，只需要定义一套
    UNLIMITED("不限", "0"),
    TODAY("今天", "1"),
    YESTERDAY("昨天", "2"),
    TOWEEK("本周", "3"),
    LASTWEEK("上周", "4"),
    TOMONTH("本月", "5"),
    LASTMONTH("上月", "6"),
    TOQUARTER("本季度", "7"),
    LASQUARTER("上季度", "8"),
    TOYEAR("本年", "9"),
    LASYEAR("去年", "10"),;


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
