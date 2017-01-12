package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class PaginationX<T> implements Serializable {
    public int pageIndex; //当前已经加载了数据的页码
    public int pageSize; //分页大小
    public int totalRecords; //全部记录总数
    public ArrayList<T> records = new ArrayList<>();//用来存放所有的已经加载的数据

    public PaginationX() {
        pageIndex = 0;
        pageSize = 20;
    }

    public PaginationX(int _pageSize) {
        pageIndex = 0;
        pageSize = _pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * 刷新的时候，通过本函数设置第一页的页码
     */
    public void setFirstPage() {
        pageIndex = 0;
    }

//    /**
//     * 判断是不是第一页，一般用在刷新的时候，如果是第一页，就需要清空原来的数据
//     *
//     * @return
//     */
//    public boolean isFirstPage() {
//        return 0 == pageIndex;
//    }

    /**
     * 当加载更多（加载下一页的时候），通过本函数获取应该加载的页码
     * 需要和＃setPageLoadSuccess()配合使用
     *
     * @return
     */
    public int getShouldLoadPageIndex() {
        return pageIndex + 1;
    }


    /**
     * 添加加载的数据进来，已经自动处理刷新哈下拉更新的问题
     * @param paginationX
     */
    public void loadRecords(PaginationX<T> paginationX) {
        if (!isEmpty(paginationX)) {
            //如果加载的是第一页的数据，就是刷新，会清空原来的，再添加新数据
            if (1 == paginationX.getPageIndex()) {
                records.clear();
            }
            //直接把数据添加进去
            records.addAll(paginationX.getRecords());
            //更新页码为当前页码
            pageIndex=paginationX.getPageIndex();
        }else{
            //新加载的数据为空，说明：1、下拉加载，没有更多数据了；2、刷新的时候，原来的数据被删除了
            //这里的判断方法是，在刷新的时候，会调用setFirstPage(),所以，pageIndex＝＝0；
            if(0==pageIndex){
                records.clear();
            }
        }

    }

    /**
     * 获取已经加载的数据
     *
     * @return
     */
    public ArrayList<T> getRecords() {
        return records;
    }

    /**
     * 判断是否为空,一般用在LoadLayout判断有没有数据的时候
     * @return
     */
    public boolean isEnpty(){
        return 0==records.size();
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

    //以下函数，只是以备不时之需，按照规范使用本类不会用到

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


}
