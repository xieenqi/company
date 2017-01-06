package com.loyo.oa.v2.activityui.product.model;

import com.loyo.oa.v2.beans.PaginationX;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yyy on 17/1/3.
 */

public class ProductListModel implements Serializable{

    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public Records records;

    public ProductListModel() {
        pageIndex = 1;
        pageSize = 20;
    }

    public ProductListModel(int _pageSize) {
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

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int t) {
        totalRecords = t;
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

    public class Records{
        public boolean stockEnabled;
        public ArrayList<ProductList> products;
    }

    public class ProductList{
        public String id;
        public String name;
        public float stock;

        public String getStock(){
            return stock > 1 ? (int) stock+"" : stock == 0 ? "0" : stock+"";
        }
    }
}
