package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by yyy on 16/12/29.
 */

public class StockListModel extends BaseBean{

    public ArrayList<Model> data;
    public class Model{
        public String tagItemId;
        public String tagItemName;
        public int inCrement;
        public int stock;
    }
}
