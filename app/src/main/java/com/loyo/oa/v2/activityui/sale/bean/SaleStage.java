package com.loyo.oa.v2.activityui.sale.bean;

import java.io.Serializable;

/**
 * Created by xeq on 16/5/18.
 */
public class SaleStage implements Serializable {
    public String id;
    public String name;
    public float prob;

    public boolean isSelect = false;//本地标记选择状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
