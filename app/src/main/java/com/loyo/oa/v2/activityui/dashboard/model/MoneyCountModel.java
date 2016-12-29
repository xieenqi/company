package com.loyo.oa.v2.activityui.dashboard.model;

import com.loyo.oa.v2.beans.BaseBean;

/**
 * Created by yyy on 16/12/29.
 */

public class MoneyCountModel extends BaseBean{

    public Model data;

    public class Model{
        public int totalAmount;
        public float totalNumber;
        public int targetAmount;
        public int targetNumber;
    }

}
