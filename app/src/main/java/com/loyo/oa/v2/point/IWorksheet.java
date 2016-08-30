package com.loyo.oa.v2.point;

/**
 * Created by EthanGong on 16/8/30.
 */

import com.loyo.oa.v2.activityui.clue.bean.ClueList;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface IWorksheet {


    /**
     * 我的工单列表
     *
     * 参数
     * templateId  工单类型id
     * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
     * keyword     关键字查询
     * type tab    1:我创建的 2:我分派的
     * pageIndex
     * pageSize
     *
     */
    @GET("/worksheets/self")
    void getMyWorksheetlist(@QueryMap HashMap<String, Object> map, Callback<ClueList> callback);

    /**
     * 团队工单列表
     *
     * 参数
     * templateId  工单类型id
     * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
     * keyword     关键字查询
     * type tab    1:我创建的 2:我分派的
     * pageIndex
     * pageSize
     * xpath       部门xpath
     * userid      用户id
     *
     */
    @GET("/worksheets/self")
    void getTeamWorksheetlist(@QueryMap HashMap<String, Object> map, Callback<ClueList> callback);
}
