package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :动态字段
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class ExtraData implements Serializable {
    private ExtraProperties properties;
    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public ExtraProperties getProperties() {
        return properties;
    }

    public void setProperties(ExtraProperties properties) {
        this.properties = properties;
    }
}
