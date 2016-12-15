package com.loyo.oa.dropdownmenu.filtermenu;

/**
 * Created by xeq on 16/12/12.
 */

public enum DashboardSortType {
    AUGMENTER_BID_SMALL("增量由大道小", "1"),
    AUGMENTER_SMALL_BIG("增量由小道大", "2"),
    STOCK_BID_SMALL("存量由大道小", "3"),
    STOCK_SMALL_BIG("存量由小道大", "4");

    public String key;
    public String value;

    DashboardSortType(String value, String key) {
        this.key = key;
        this.value = value;
    }
}
