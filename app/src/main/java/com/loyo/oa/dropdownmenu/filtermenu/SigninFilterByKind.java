package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * 【我的拜访】类型
 * Created by yyy on 2016/10/31.
 */

public enum SigninFilterByKind {

    ALL("全部","one"),
    SELF("我创建的","tweo"),
    CALLME("@我的","three");


    public String key;
    public String value;
    SigninFilterByKind(String value, String key) {
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
