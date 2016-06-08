package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;

/**
 * 意向产品 add
 * Created by xeq on 16/5/23.
 */
public class SaleIntentionalProduct implements Serializable {

    public String id;
    public String name;
    public double costPrice;
    public double salePrice;
    public double quantity;
    public double discount;
    public double totalMoney;
    public String memo;
    public String unit;

}
