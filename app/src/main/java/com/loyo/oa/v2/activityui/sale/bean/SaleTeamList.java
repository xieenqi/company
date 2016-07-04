package com.loyo.oa.v2.activityui.sale.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 机会团队列表bean
 * Created by yyy on 16/5/20.
 */
public class SaleTeamList implements Serializable{

    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public ArrayList<SaleRecord> records = new ArrayList<>();

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public ArrayList<SaleRecord> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<SaleRecord> records) {
        this.records = records;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
