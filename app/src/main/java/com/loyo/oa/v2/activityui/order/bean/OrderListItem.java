package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;

/**
 * 我的订单 团队订单 单个item
 * Created by xeq on 16/8/3.
 */
public class OrderListItem implements Serializable {
    public String id;
    public String title;
    public String customerName;
    public String directorName;
    public String proName;
    public int status;
    public long dealMoney;
    public long createdAt;

}
