package com.loyo.oa.v2.common;

/**
 * Created by EthanGong on 16/8/27.
 */
public interface Groupable<T> extends Comparable<T> {

    /* 分组字段 */
    public abstract GroupKey groupBy();
}
