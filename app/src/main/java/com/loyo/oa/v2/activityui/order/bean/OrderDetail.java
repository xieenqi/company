package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;

/**
 * Created by xeq on 16/8/3.
 */
public class OrderDetail implements Serializable {
    public String id;
    public String companyId;
    public String title;
    public String customerName;
    public String directorName;
    public String creatorName;
    public int status;
    public long dealMoney;
    public int backMoney;
    public int ratePayment;
    public int salesAmount;
    public int discount;
    public int attachmentCount;
    public int recordNum;
    public int planNum;
    public long createdAt;
    public String proName;
}
