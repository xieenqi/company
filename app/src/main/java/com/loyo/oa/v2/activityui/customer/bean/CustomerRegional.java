package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户区域  地区公用数据模块
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class CustomerRegional implements Serializable {
    public String province;
    public String city;
    public String county;

    public String salesleadDisplayText() {
        return province + " " + city + " " +  county;
    }

    public String[] toArray() {
        String[] result = new String[3];

        result[0]= province!=null?province:"";
        result[1]= city!=null?city:"";
        result[2]= county!=null?county:"";
        return result;
    }
}
