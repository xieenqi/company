package com.loyo.oa.v2.activityui.signin.bean;

import com.loyo.oa.v2.beans.TeamLegwork;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :团队拜访分页
 * 作者 : ykb
 * 时间 : 15/10/29.
 */
public class PaginationLegWork implements Serializable {
    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public TeamLegwork records;

    public PaginationLegWork() {
        pageIndex = 1;
        pageSize = 20;
    }

    public PaginationLegWork(int _pageSize) {
        pageIndex = 1;
        pageSize = _pageSize;
    }


}
