package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * Created by xeq on 16/12/12.
 */

public enum DashboardSortType {
    AUGMENTER_BID_SMALL("增量由大到小", "1"),
    AUGMENTER_SMALL_BIG("增量由小到大", "2"),
    STOCK_BID_SMALL("存量由大到小", "3"),
    STOCK_SMALL_BIG("存量由小到大", "4"),


    NUMBER_DROP("次数由大到小", "1"),
    NUMBER_RISE("次数由小到大", "2"),
    CUS_DROP("客户数由大到小", "3"),
    CUS_RISE("客户数由小到大", "4"),

    SALE_DROP("线索数由大到小", "3"),
    SALE_RISE("线索数由小到大", "4"),

    RECORD_DROP("通话时间由长到短", "5"),
    RECORD_RISE("通话时间由短到长", "6"),

    ORDER_NUMBER_DROP("订单数量由大到小", "1"),
    ORDER_NUMBER_RISE("订单数量由小到大", "2"),

    ORDER_MONEY_DROP("订单金额由大到小", "1"),
    ORDER_MONEY_RISE("订单金额由小到大", "2"),

    COMPILE_DROP("完成率由大到小","3"),
    COMPILE_RISE("完成率由小到大","4");

    public String key;
    public String value;

    DashboardSortType(String value, String key) {
        this.key = key;
        this.value = value;
    }
}
