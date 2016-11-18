package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * 【我的拜访】类型
 * Created by yyy on 2016/10/31.
 */

public enum SigninFilterBySort {

    SORT1("按时间倒序","0"),
    SORT2("按偏差距离由大到小","1"),
    SORT3("按偏差距离有小到大","2");


    public String key;
    public String value;
    SigninFilterBySort(String value, String key) {
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
