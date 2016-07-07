package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;


public class Role implements Serializable {
    public static final int NONE = 0;
    public static final int ALL = 1;            //全公司
    public static final int DEPT_AND_CHILD = 2; //部门
    public static final int SELF = 3;           //个人
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
