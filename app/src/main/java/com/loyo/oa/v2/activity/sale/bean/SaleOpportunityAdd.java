package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 创建销售机会 的ben
 * Created by xeq on 16/5/23.
 */
public class SaleOpportunityAdd implements Serializable {
    public String customerName;

    public String customerId;

    public String name;

    public String stageId;

    public float estimatedAmount;//预估金额

    public int estimatedTime;//预估成交时间

    public ArrayList<SaleIntentionalProduct> proInfos;

    public String changeSource;

    public String changeType;

    public String memo;

//public extensionDatas;//动态字段
//{
//    "id": "5742cb5f526f155238848b5e",
//        "creatorId": "573ad56935d860415ce09341",
//        "customerId": "573d7c31526f1554507904e6",
//        "name": "看看",
//        "stageId": "573d99b9526f1554507904eb",
//        "prob": 0,
//        "estimatedAmount": 112,
//        "salesAmount": 1980,
//        "discount": 3.46,
//        "chanceType": "",
//        "proInfos": [
//    {
//        "id": "573c30ff35d8603d2933cb72",
//            "name": "企业QQ",
//            "costPrice": 1300,
//            "salePrice": 45,
//            "quantity": 44,
//            "discount": 3.4615386,
//            "totalMoney": 1980,
//            "memo": ""
//    }
//    ],
//    "lostReason": "",
//        "estimatedTime": 1463932800,
//        "winTime": 0,
//        "createdAt": 1463995231,
//        "updatedAt": 1463995231
//}
}
