package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBean;

/**
 * Created by yyy on 16/12/29.
 */

public class StockStatistic extends BaseBean{

    public String tagItemId;
    public String tagItemName;
    public int inCrement;
    public int stock;

    public String getStock() {
        return String.valueOf(stock);
    }

    public String getIncement() {
        return String.valueOf(inCrement);
    }
}
