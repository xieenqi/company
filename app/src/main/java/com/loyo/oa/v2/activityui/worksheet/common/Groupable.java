package com.loyo.oa.v2.activityui.worksheet.common;

import java.util.Map;

/**
 * Created by EthanGong on 16/8/27.
 */
public interface Groupable<T> {

    /* 分组字段 */
    public abstract T groupBy();
}
