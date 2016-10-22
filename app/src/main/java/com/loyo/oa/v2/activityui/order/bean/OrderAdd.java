package com.loyo.oa.v2.activityui.order.bean;

import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 【订单新建】
 * Created by yyy on 16/8/3.
 */
public class OrderAdd implements Serializable{

    public String customerId;    //客户ID
    public String title;         //订单标题
    public String customerName;  //对应客户
    public String attachmentUUId;

    public float  dealMoney;     //成交金额
    public String orderNum;   //订单编号
    public String remark;     //备注

    public ArrayList<SaleIntentionalProduct> proInfo; //产品
    public ArrayList<EstimateAdd>     paymentRecords; //回款

}
