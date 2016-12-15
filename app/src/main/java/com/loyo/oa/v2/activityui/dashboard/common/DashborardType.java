package com.loyo.oa.v2.activityui.dashboard.common;

import com.loyo.oa.v2.permission.BusinessOperation;

import java.io.Serializable;

/**
 * 仪表盘的类型
 * Created by xeq on 16/12/13.
 */

public enum DashborardType implements Serializable{

    CUS_FOLLOWUP {
        @Override
        public String getTitle() {
            return "跟进动态";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }
    },
    CUS_SIGNIN {
        @Override
        public String getTitle() {
            return "客户拜访";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }
    },
    CUS_CELL_RECORD {
        @Override
        public String getTitle() {
            return "电话录音";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }
    },
    SALE_FOLLOWUP {
        @Override
        public String getTitle() {
            return "跟进动态";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }
    },
    SALE_CELL_RECORD {
        @Override
        public String getTitle() {
            return "电话录音";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }
    },
    ORDER_NUMBER {
        @Override
        public String getTitle() {
            return "订单数量";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.RESULTS_LOOK;
        }
    },
    ORDER_MONEY {
        @Override
        public String getTitle() {
            return "订单金额";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.RESULTS_LOOK;
        }
    };

    public abstract String getTitle();

    public abstract String getaPermission();

}
