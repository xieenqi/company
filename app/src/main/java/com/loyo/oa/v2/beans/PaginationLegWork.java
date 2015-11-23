package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :团队拜访分页
 * 作者 : ykb
 * 时间 : 15/10/29.
 */
public class PaginationLegWork implements Serializable {
    int pageIndex;
    int pageSize;
    TeamLegwork records;
    int totalRecords;

    public PaginationLegWork() {
        pageIndex = 1;
        pageSize = 20;
    }

    public PaginationLegWork(int _pageSize) {
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

    public TeamLegwork getRecords() {
        return records;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int t) {
        totalRecords = t;
    }

}
