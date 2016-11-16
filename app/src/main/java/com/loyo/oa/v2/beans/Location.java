package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 新建跟进 添加的定位
 * Created by xeq on 16/11/14.
 */

public class Location implements Serializable {
    public String addr;
    public List<Double> loc;

    public Location(List<Double> loc, String addr) {
        this.loc = loc;
        this.addr = addr;
    }
}
