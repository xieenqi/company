package com.loyo.oa.v2.activityui.sale.bean;

import java.io.Serializable;

/**
 * 输单原因bean
 * Created by yyy on 16/5/30.
 */
public class SaleLoseReason implements Serializable{

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }
}
