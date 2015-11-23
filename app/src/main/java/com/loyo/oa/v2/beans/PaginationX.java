package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class PaginationX<T> implements Serializable {
    int pageIndex;
    int pageSize;
    ArrayList<T> records = new ArrayList<>();
    int totalRecords;

    public PaginationX() {
        pageIndex = 1;
        pageSize = 20;
    }

    public PaginationX(int _pageSize) {
        pageIndex = 1;
        pageSize = _pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ArrayList<T> getRecords() {
        return records;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int t) {
        totalRecords =  t;
    }

    public static boolean isEmpty(PaginationX pagination) {

        if (pagination == null
                || pagination.getRecords() == null
                || "".equals(pagination)
                || "{}".equals(pagination)
                || "[]".equals(pagination)
                || pagination.getRecords().size() == 0) {
            return true;
        }

        return false;
    }
}
