package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBean;

/**
 * 客户和线索跟进
 * Created by yyy on 16/12/28.
 */

public class CsclueFowUp extends BaseBean {

    public Data data;

    class Data {
        public SaleActivity saleActivity;
        public SaleSlead saleSlead;
    }

    class SaleActivity {
        public DivModel activity;
        public DivModel visit;
        public DivModel voice;
    }

    class SaleSlead {
        public DivModel activity;
        public DivModel voice;
    }

    class DivModel {
        String name;
        int totalCount;
        int distinctCount;
    }
}
