package com.loyo.oa.v2.activityui.clue.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * 线索来源 bean
 * Created by xeq on 16/8/22.
 */
public class SourcesData extends BaseBean {
    public Data data;

    public class Data {
        public ArrayList<IdName> records;
    }
}
