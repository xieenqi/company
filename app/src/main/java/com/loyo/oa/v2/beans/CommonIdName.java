package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by xeq on 16/11/16.
 */

public class CommonIdName implements Serializable {
    public String id;
    public String name;

    public CommonIdName(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
