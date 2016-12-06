package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetListWrapper extends BaseBean {

    public WorksheetList data;


    public class WorksheetList {
        public int pageIndex;
        public int pageSize;
        public int totalRecords;
        public ArrayList<Worksheet> records;
    }

    public boolean isEmpty() {
        if (data == null) {
            return true;
        } else if (data.records == null) {
            return true;
        } else if (data.records.size() == 0) {
            return true;
        }
        return false;
    }
}
