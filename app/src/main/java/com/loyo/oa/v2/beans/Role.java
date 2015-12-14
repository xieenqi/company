package com.loyo.oa.v2.beans;

import java.io.Serializable;


public class Role implements Serializable {
    public final static int NONE = 0;
    public final static int ALL = 1;
    public final static int DEPT_AND_CHILD = 2;
    public final static int SELF = 3;
    public String id;
    public String name;
    public int dataRange;
    public int  customerNumLimit;


    public int getDataRange() {
        return dataRange == 0 ? SELF : dataRange;
    }

    public void setDataRange(int dataRange) {
        this.dataRange = dataRange;
    }

}
