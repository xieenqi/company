package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户位置信息
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class Locate implements Serializable {

    public String addr;

    public double[] loc = new double[2];

    public double[] getLoc() {
        return null == loc ? new double[2] : loc;
    }

    public void setLoc(double[] loc) {
        this.loc = loc;
    }

    public boolean validatedLocation() {
        return loc.length > 1
                && !(loc[0] == 0.0 && loc[1] == -90.0);
    }

}
