package com.loyo.oa.v2.activityui.wfinstance.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/7/21.
 * 我提交的 列表数据模板
 */
public class MySubmitWflnstance implements Serializable {
    public int pageIndex;
    public int pageSize;
    public ArrayList<WflnstanceListItem> records;


}
