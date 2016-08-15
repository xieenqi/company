package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/8/8.
 */
public class EstimateList implements Serializable {

    public ArrayList<EstimateAdd> records;
    public Total total;

    public class Total{
        public float proMoney;   //产品金额
        public float dealMoney;  //成交金额
        public float backMoney;  //回款金额
        public float billingMoney; //开票总金额
        public float notBackMoney; //未回款金额
        public String backMoneyRate; //回款率

    }
}
