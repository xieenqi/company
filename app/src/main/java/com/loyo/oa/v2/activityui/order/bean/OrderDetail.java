package com.loyo.oa.v2.activityui.order.bean;

import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xeq on 16/8/3.
 */
public class OrderDetail implements Serializable {
    public String id;
    public String companyId;
    public String customerId;
    public String title;
    public String customerName;
    public String directorName;
    public String creatorName;
    public int status;
    public long dealMoney;
    public int backMoney;
    public int ratePayment;
    public double salesAmount;
    public double discount;
    public int attachmentCount;
    public int recordNum;
    public int planNum;
    public long createdAt;
    public String proName;
    public String wfId;
    public String orderNum;
    public String remark;
    public List<ExtensionDatas> extensionDatas;
    public ArrayList<SaleIntentionalProduct> proInfo;
}
