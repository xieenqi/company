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

    //客户跟进
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

        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","次数","客户数"};
        }
    },
    // 客户拜访
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","次数","客户数"};
        }

    },
    //客户电话录音
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","次数","客户数","通话时长"};
        }
    },
    //线索 跟进
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","次数","线索数"};
        }
    },
    //线索电话录音
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","次数","线索数","通话时长"};
        }
    },
    //订单数量
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","目标数量","订单数量","完成率"};
        }
    },
    //订单金额
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","目标金额","订单金额","完成率"};
        }
    },
    //存量／增量
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
        @Override
        public String[] getTableHead() {
            return new String[]{"姓名","增量","存量"};
        }
    };

    public abstract String getTitle();
    public abstract @BusinessOperation.Type String getaPermission();
    public abstract FilterModel getSort();
    //获取表头标题
    public abstract String[] getTableHead();

    String tttle;
    String permission;

    public void setTttle(String tttle) {
        this.tttle = tttle;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


}
