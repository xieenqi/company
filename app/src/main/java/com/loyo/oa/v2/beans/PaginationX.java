package com.loyo.oa.v2.beans;

import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 分页实体处理类
 * 带分页，全部使用本类，自动处理好页码的问题，使用方法如下：
 * 加载第一页或者初始化的时候，调用＃setFirstPage()
 * 加载更多不用处理，
 * 加载的时候，pageIndex使用＃getShouldLoadPageIndex()，pageSize使用：＃getPageSize()
 * 加载完成以后，调用#loadRecords(data)把新页的数据加进来。
 * 取用数据使用getRecords()
 * 判断当前数据为为空使用：＃isEnpty()
 * @param <T>
 */
public class PaginationX<T> implements Serializable {
    //一下字段是model
    public int pageIndex; //当前已经加载了数据的页码
    public int pageSize; //分页大小
    public int totalRecords; //全部记录总数
    public ArrayList<T> records = new ArrayList<>();//用来存放所有的已经加载的数据

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

    /**
     * 刷新的时候，通过本函数设置第一页的页码
     */
    public void setFirstPage() {
        pageIndex = 0;
    }

    /**
     * 刷新，或者重新加载数据的时候，需要把列表返回top
     * 也就是，加载的是第一页，就返回top
     * @return
     */
    public boolean isNeedToBackTop(){
        return 1==pageIndex;
    }

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
                //刷新没有数据
                records.clear();
            }else{
                //加载更多没有数据
                Toast.makeText(MainApp.getMainApp(),"没有数据了",Toast.LENGTH_LONG).show();
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
        return records.size();
    }

    public void setTotalRecords(int t) {
        totalRecords = t;
    }


}
