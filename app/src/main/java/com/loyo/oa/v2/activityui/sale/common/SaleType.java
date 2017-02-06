package com.loyo.oa.v2.activityui.sale.common;

/**
 * 销售机会类型
 */

public enum SaleType {
    MY_SALE_SEARCH(0),
    TEAM_SALE_SEARCH(1);

    public int type;

    SaleType(int type) {
        this.type=type;
    }
}
