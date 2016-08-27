package com.loyo.oa.v2.activityui.worksheet.common;

import java.util.Map;

/**
 * Created by EthanGong on 16/8/27.
 */
public abstract class Groupable<T> {

    /* 分组字段 */
    public abstract T groupBy();

    /* 排序规则 */
    public static Map sortMap()
    {
        return null;
    }
}
