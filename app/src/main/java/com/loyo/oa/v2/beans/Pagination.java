package com.loyo.oa.v2.beans;

import com.google.gson.JsonArray;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/18 0018.
 */
//@Deprecated
public class Pagination implements Serializable {
    private int pageIndex;// (int64, optional): ,//当前页
    private int pageSize;// (int64, optional): ,//当前页记录数
    private JsonArray records;// (&{1294 0xc20815f770 false}, optional): ,
    private int totalRecords;//(int64, optional)://总记录数

    public Pagination() {
        pageIndex = 0;
        pageSize = 20;
    }

    public Pagination(int _pageSize) {
        pageIndex = 0;
        pageSize = _pageSize;
    }

    public  void clear() {
        pageIndex = 0;
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

    public JsonArray getRecords() {
        return records;
    }

    public void setRecords(JsonArray records) {
        this.records = records;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public static boolean isEmpty(Pagination pagination) {

        if (pagination == null
                || "".equals(pagination)
                || "{}".equals(pagination)
                || "[]".equals(pagination)
                || pagination.getRecords() == null
                || pagination.getRecords().size() == 0) {
            return true;
        }

        return false;
    }
}
