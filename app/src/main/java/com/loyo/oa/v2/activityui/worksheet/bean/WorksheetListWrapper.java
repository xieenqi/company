package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorkSheetListWrapper extends BaseBean {

    public WorksheetList data;


    public class WorksheetList {
        public int pageIndex;
        public int pageSize;
        public int totalRecords;
        public ArrayList<WorkSheet> records;
    }
}
