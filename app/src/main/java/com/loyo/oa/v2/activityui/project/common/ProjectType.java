package com.loyo.oa.v2.activityui.project.common;

/**
 * Created by EthanGong on 2016/11/1.
 */

public enum ProjectType {
//    private static final String[] FILTER_TYPE_ARRAY = new String[]{"全部类型", "我负责", "我创建", "我参与"};
//    private static final int[] FILTER_TYPEID_ARRAY = new int[]{0, 3, 2, 1};

    ALL(0, "0", "不限类型"),
    PARTICIPATE_IN(1, "1", "我参与"),
    CREATE_BY_ME(2, "2", "我创建"),
    IN_CHARGE(3, "3", "我负责");

    public int code;
    public String key;
    public String value;

    ProjectType(int code, String key, String value) {
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
