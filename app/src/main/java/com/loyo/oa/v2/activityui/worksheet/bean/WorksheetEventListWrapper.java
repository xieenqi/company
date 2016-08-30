package com.loyo.oa.v2.activityui.worksheet.bean;

import com.loyo.oa.v2.beans.BaseBean;

import java.util.ArrayList;

/**
 * Created by EthanGong on 16/8/30.
 */
public class WorksheetEventListWrapper extends BaseBean {

    public WorksheetEventList data;

    public class WorksheetEventList {
        public int pageIndex;
        public int pageSize;
        public int totalRecords;

        public ArrayList<WorksheetEvent> records;
    }
}
