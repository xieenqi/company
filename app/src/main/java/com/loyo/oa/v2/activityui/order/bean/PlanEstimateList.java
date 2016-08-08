package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;

/**
 * 【回款计划】
 * Created by yyy on 16/8/5.
 */
public class PlanEstimateList implements Serializable{

    public String  id;
    public String  orderId;
    public int planAt;
    public String companyId;
    public String remark;
    public float planMoney;
    public int payeeMethod;
    public int remindAt;
    public int remindType;
    public int createdAt;
    public int updatedAt;

}
