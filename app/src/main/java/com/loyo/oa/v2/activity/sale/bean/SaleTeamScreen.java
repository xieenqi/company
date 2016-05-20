package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/5/18.
 */
public class SaleTeamScreen implements Serializable{

    public String name;
    public String id;
    public boolean index;

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
