package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/8/8.
 */
public class EstimateList implements Serializable{

    public ArrayList<EstimateAdd> records;

    public class Total{
        int proMoney;
        int dealMoney;
        int backMoney;
        int billingMoney;
        int notBackMoney;
        String backMoneyRate;
    }
}
