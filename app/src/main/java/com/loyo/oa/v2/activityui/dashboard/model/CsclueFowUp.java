package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBean;

/**
 * 客户和线索跟进
 * Created by yyy on 16/12/28.
 */

public class CsclueFowUp extends BaseBean {

    public Data data;

    public class Data {
        public DsFowUpCustomer saleActivity;
        public DsFowUpSale salesLead;
    }

    public class DsFowUpCustomer {
        public DivModel activity;
        public DivModel visit;
        public DivModel voice;
    }
    public class DsFowUpSale {
        public DivModel activity;
        public DivModel voice;
    }

    public class DivModel {
        public String name;
        public int totalCount;
        public int distinctCount;
    }
}
