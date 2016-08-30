package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetListWrapper {

    public WorksheetList data;


    public class WorksheetList extends BaseBean {
        public int pageIndex;
        public int pageSize;
        public int totalRecords;
        public ArrayList<Worksheet> records;
    }
}
