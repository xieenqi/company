package com.loyo.oa.v2.activity.home.bean;

import java.io.Serializable;

/**
 * 销售漏斗
 * Created by xeq on 16/6/17.
 */
public class HttpSalechance implements Serializable {
    public String stageId;
    public String stageName;
    public double totalMoney;
    public double totalNum;
    public double prob;
}
