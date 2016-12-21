package com.loyo.oa.v2.worksheet.api;

import com.loyo.oa.v2.activityui.worksheet.bean.EventDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetail;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/20.
 */

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
    Observable<BaseResponse<PaginationX<Worksheet>>> getMyWorksheetList(@QueryMap HashMap<String, Object> map);

    /**
     * 我负责的列表
     * <p/>
     * 参数
     * templateId  工单类型id
     * status      1:待处理 2:未触发 3:已处理 ?4:意外中止
     * keyword     关键字查询
     * pageIndex
     * pageSize
     */
    @GET("/worksheet/event/mobile")
    Observable<BaseResponse<PaginationX<WorksheetEvent>>> getResponsableWorksheetList(@QueryMap HashMap<String, Object> map);

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
    @GET("/worksheets/team")
    Observable<BaseResponse<PaginationX<Worksheet>>> getTeamWorksheetList(@QueryMap HashMap<String, Object> map);

    /**
     * 团队工单列表
     * <p/>
     * 参数
     * oid 具体订单号
     * pageIndex
     * pageSize
     */
    @GET("/worksheets/order/{oid}")
    Observable<BaseResponse<PaginationX<Worksheet>>> getWorksheetListByOrder(@Path("oid") String oid, @QueryMap HashMap<String, Object> map);


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
     */
    @POST("/worksheets")
    Observable<BaseResponse<Worksheet>> addWorksheet(@Body Map<String, Object> body);

    /**
     * 工单类型列表
     * <p/>
     * 参数 无
     */
    @GET("/worksheet/setting")
    Observable<BaseResponse<ArrayList<WorksheetTemplate>>> getWorksheetTypesList();

    /**
     * 获取工单详细信息
     */
    @GET("/worksheets/{id}")
    Observable<BaseResponse<WorksheetDetail>> getWorksheetDetail(@Path("id") String id);

    /**
     * 设置事件 负责人
     */
    @PUT("/worksheet/event/responsor/{id}")
    Observable<BaseResponse<Object>> setEventPerson(@Path("id") String id, @Body Map<String, Object> body);

    /**
     * 设置所有事件 批量设置负责人
     */
    @PUT("/worksheet/event/responsor")
    Observable<BaseResponse<Object>> setAllEventPerson(@Body Map<String, Object> body);

    /**
     * 订单列表
     * 参数
     * pageIndex
     * pageSize
     * keyword 订单标题关键字查询
     */
    @GET("/order/work/list")
    Observable<BaseResponse<PaginationX<WorksheetOrder>>> getWorksheetOrdersList(@QueryMap HashMap<String, Object> map);

    /**
     * 提交事件处理信息
     */
    @PUT("/worksheet/event/submit/{id}")
    Observable<BaseResponse<Object>> setEventSubmit(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 获取工单信息
     */
    @GET("/worksheets/detail/{id}")
    Observable<BaseResponse<WorksheetInfo>> getWorksheetInfo(@Path("id") String id);

    /**
     * 获取 工单 事件详情  /worksheet/event/mobile/:id?wsId=工单id
     */
    @GET("/worksheet/event/mobile/{id}")
    Observable<BaseResponse<EventDetail>> getEventDetail(@Path("id") String id, @QueryMap HashMap<String, Object> map);


    /**
     * 更新工单状态 意外终止工单
     */
    @PUT("/worksheets/{id}")
    Observable<BaseResponse<Object>> setStopWorksheet(@Path("id") String id, @Body HashMap<String, Object> map);
}
