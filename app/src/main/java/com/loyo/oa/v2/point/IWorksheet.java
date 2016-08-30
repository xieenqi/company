package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetial;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplateListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetWrapper;
import com.loyo.oa.v2.beans.BaseBeanT;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface IWorksheet {


    /**
     * 我的工单列表
     * <p/>
     * 参数
     * templateId  工单类型id
     * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
     * keyword     关键字查询
     * type tab    1:我创建的 2:我分派的
     * pageIndex
     * pageSize
     */
    @GET("/worksheets/self")
    void getMyWorkSheetlist(@QueryMap HashMap<String, Object> map, Callback<WorksheetListWrapper> callback);

    /**
     * 团队工单列表
     * <p/>
     * 参数
     * templateId  工单类型id
     * status      1:待分派 2:处理中 3:待审核 4:已完成 5:意外中止
     * keyword     关键字查询
     * type tab    1:我创建的 2:我分派的
     * pageIndex
     * pageSize
     * xpath       部门xpath
     * userid      用户id
     */
    @GET("/worksheets/self")
    void getTeamWorkSheetList(@QueryMap HashMap<String, Object> map, Callback<WorksheetListWrapper> callback);


    /**
     * 新建工单
     *
     * @param body     {
     *                 "title"      :"工单001",
     *                 "orderId"    :"57c3ef26ebe07f2d0b000001",
     *                 "orderName"  :"新建的工单",
     *                 "templateId" :"57c3ef26ebe07f2d0b000001",
     *                 "content"    : "工单事件很多哟!",
     *                 "uuid"       :"57c3ef26ebe07f2d0b000001"
     *                 }
     * @param callback
     */
    @POST("/worksheets")
    void addWorkSheet(@Body Map<String, Object> body, Callback<WorksheetWrapper> callback);

    /**
     * 工单类型列表
     * <p/>
     * 参数 无
     */
    @GET("/worksheet/setting")
    void getWorkSheetTypesList(Callback<WorksheetTemplateListWrapper> callback);

    /**
     * 获取工单详细信息
     */
    @GET("/worksheets/{id}")
    void getWorksheetDetail(@Path("id") String id, Callback<BaseBeanT<WorksheetDetial>> callback);

    /**
     * 设置事件 负责人
     */
    @PUT("worksheet/event/responsor/{id}")
    void setEventPerson(@Path("id") String id, @Body Map<String, Object> body, Callback<Object> callback);
}
