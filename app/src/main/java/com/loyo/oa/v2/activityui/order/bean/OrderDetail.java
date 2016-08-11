package com.loyo.oa.v2.activityui.order.bean;

import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/8/3.
 */
public class OrderDetail implements Serializable {

    public String attachmentUUId;
    public String id;
    public String companyId;
    public String customerId;
    public String title;
    public String customerName;
    public String directorName;
    public String creatorName;
    public int status;//1待审核 2未通过 3进行中 4已完成 5意外终止 6
    public float dealMoney;//成交金额
    public float planMoney;//计划回款金额
    public int backMoney;
    public double ratePayment;//回款率
    public double salesAmount;
    public double discount;
    public int attachmentCount;
    public int recordNum;
    public int planNum;
    public long createdAt;
    public String proName;
    public String wfId;
    public String orderNum;//订单编号
    public String remark;
    public ArrayList<EstimateAdd> paymentRecords;
    //public List<ExtensionDatas> extensionDatas;
    public ArrayList<ContactLeftExtras> extensionDatas;
    public ArrayList<SaleIntentionalProduct> proInfo;
    public String wfName;
    public String directorId;//负责人id


}
