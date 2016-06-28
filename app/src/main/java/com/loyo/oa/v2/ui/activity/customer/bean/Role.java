package com.loyo.oa.v2.ui.activity.customer.bean;

import java.io.Serializable;


public class Role implements Serializable {
    public static final int NONE = 0;
    public static final int ALL = 1;
    public static final int DEPT_AND_CHILD = 2;
    public static final int SELF = 3;
    public String id;
    public String name;
    public int dataRange;
    public int customerNumLimit;


    public int getDataRange() {
        return dataRange == 0 ? SELF : dataRange;
    }

    public void setDataRange(int dataRange) {
        this.dataRange = dataRange;
    }

}
