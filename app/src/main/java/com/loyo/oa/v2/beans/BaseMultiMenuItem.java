package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :多级联动菜单item
 * 作者 : ykb
 * 时间 : 15/10/19.
 */
public abstract class BaseMultiMenuItem implements Serializable {
    protected String name;
    protected String id;

    public abstract ArrayList<? extends BaseMultiMenuItem> getChildren();

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

    protected abstract boolean hasChildren();
}
