package com.loyo.oa.v2.activityui.order.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/8/8.
 */
public class EstimateList implements Serializable {

    public ArrayList<EstimateAdd> records;
    public Total total;

    public class Total {
        public double proMoney;//产品金额
        public double dealMoney;//成交金额
        public double backMoney;//回款金额
        public double billingMoney;//开票金额
        public double notBackMoney;//未回款金额
        public String backMoneyRate;//回款率
    }
}
