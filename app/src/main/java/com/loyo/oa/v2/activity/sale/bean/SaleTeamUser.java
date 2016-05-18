package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/5/18.
 */
public class SaleTeamUser implements Serializable{

    public String name;
    public String id;

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
