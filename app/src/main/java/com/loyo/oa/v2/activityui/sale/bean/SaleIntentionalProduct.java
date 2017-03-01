package com.loyo.oa.v2.activityui.sale.bean;

import java.io.Serializable;

/**
 * 意向产品 add
 * Created by xeq on 16/5/23.
 */
public class SaleIntentionalProduct implements Serializable {

    public String id;
    public String name;      /* 产品名称 */
    public double costPrice; /* 销售价格 */
    public double salePrice; /* 原价 */
    public double quantity;  /* 数量 */
    public double stock;     /* 库存 */
    public double discount;  /* 折扣，已乘100*/
    public double totalMoney;/* 总金额，实际销售的总额， 等于 costPrice 乘 quantity */
    public String memo;      /* 备注 */
    public String unit;      /* 单位 */

}
