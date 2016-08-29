package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by pj on 15/3/22.
 */
public abstract class BaseBeans implements Serializable{
    public abstract String getOrderStr();

    public abstract String getId();

    public String getOrderStr2() {
        return "0";
    }
}
