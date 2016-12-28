package com.loyo.oa.v2.activityui.dashboard.common;

import com.loyo.oa.dropdownmenu.filtermenu.DashboardSortTypeMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.v2.permission.BusinessOperation;
import java.io.Serializable;

/**
 * 仪表盘的类型
 * Created by xeq on 16/12/13.
 */

public enum DashborardType implements Serializable {

    CUS_FOLLOWUP {
        @Override
        public String getTitle() {
            return "跟进动态";
        }

        @Override
        public String getaPermission() {
            return BusinessOperation.FOLLOWUP_STATISTICS;
        }

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getCusFolloupFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getCusFolloupFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getCusRecordFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getSaleFolloupFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getSaleRecordFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getOrderNumberFilterModel();
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

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getOrderMoneyFilterModel();
        }
    },
    COMMON {
        @Override
        public String getTitle() {
            return tttle;
        }

        @Override
        public String getaPermission() {
            return permission;
        }

        @Override
        public FilterModel getSort() {
            return DashboardSortTypeMenuModel.getFilterModel();
        }
    };

    public abstract String getTitle();

    public abstract String getaPermission();
    public abstract FilterModel getSort();

    String tttle;
    String permission;

    public void setTttle(String tttle) {
        this.tttle = tttle;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


}
