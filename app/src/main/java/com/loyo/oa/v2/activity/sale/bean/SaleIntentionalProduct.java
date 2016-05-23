package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;

/**
 * 意向产品 add
 * Created by xeq on 16/5/23.
 */
public class SaleIntentionalProduct implements Serializable {

    public String id;
    public String name;
    public float costPrice;
    public float salePrice;
    public float quantity;
    public float discount;
    public float totalMoney;
    public String memo;

    //Id         bson.ObjectId `bson:"_id,omitempty" json:"id,omitempty"` //产品id
//    Name       string        `bson:"name" json:"name"`                  //产品名称
//    CostPrice  float64       `bson:"cost_price" json:"costPrice"`       //原价
//    SalePrice  float64       `bson:"sale_price" json:"salePrice"`       //销售价格
//    Quantity   float64       `bson:"quantity" json:"quantity"`          //数量
//    Discount   float64       `bson:"discount" json:"discount"`          //折扣
//    TotalMoney float64       `bson:"total_money" json:"totalMoney"`     //总金额
//    Memo       string        `bson:"memo" json:"memo"`
}
