package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :动态字段
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class ExtraDataforContact implements Serializable {
    private ExtraProperties properties;

    public ExtraProperties getProperties() {
        return properties;
    }

    public void setProperties(ExtraProperties properties) {
        this.properties = properties;
    }
}
