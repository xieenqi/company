package com.loyo.oa.v2.activityui.clue.api;

import com.loyo.oa.v2.activityui.clue.model.ClueDetailWrapper;
import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueSales;
import com.loyo.oa.v2.activityui.clue.model.SourcesData;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by jie on 16/12/20.
 */

public interface IClue {
    /**
     * 我的线索列表
     */
    @GET("/salesleads/mobile")
    Observable<ClueList> getMyClueList(@QueryMap HashMap<String, Object> map);

    /**
     * 团队线索列表
     */
    @GET("/salesleads/mobile/team")
    Observable<ClueList> getTeamClueList(@QueryMap HashMap<String, Object> map);

    /**
     * 线索下的 跟进
     */
    @GET("/saleactivity/saleslead")
    Observable<PaginationX<ClueFollowGroupModel>> followUp(@QueryMap Map<String, Object> params);

    /**
     * 获取 线索来源
     */
    @GET("/saleslead/source")
    Observable<SourcesData> getSource();

//    /**
//     * 新建线索 表单传输
//     */
//    @POST("/salesleads")
//    Observable<ClueDetailWrapper> addClue(@Body Map<String, Object> body);


    /**
     * 新建线索 表单传输
     */
    @POST("/salesleads")
    Observable<BaseResponse<ClueSales>> addClueNew(@Body Map<String, Object> body);


    /**
     * 获取 线索详情
     */
    @GET("/salesleads/mobile/{id}")
    Observable<BaseBeanT<ClueDetailWrapper.ClueDetail>> getClueDetail(@Path("id") String id);

    /**
     * 编辑 线索
     */
    @PUT("/salesleads/{id}")
    Observable<Object> editClue(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 删除
     */
    @PUT("/salesleads/batch/delete")
    Observable<Object> deleteClue(@Body HashMap<String, Object> map);


    /**
     * 转移 线索/salesleads/batch/transfer
     */
    @PUT("/salesleads/batch/transfer")
    Observable<Object> transferClue(@Body HashMap<String, Object> map);

    /**
     * 线索 拨打商务电话 【回拨】信息查询
     */
    @POST("/ipvoice/request")
    Observable<CallBackCallid> getCallReturnInfo(@Body HashMap<String, Object> map);


    /**********下接口存在，但是没有使用*******/


    /**
     * 获取跟进
     */
    @GET("/saleactivity/saleslead")
    Observable<PaginationX<SaleActivity>> getSaleactivity(@QueryMap HashMap<String, Object> map);

    /**
     * 线索写 新建跟进
     */
    @POST("/saleactivity/sealslead")
    Observable<SaleActivity> addSaleactivity(@Body HashMap<String, Object> map);
    /**
     * 线索 拨打商务电话 【直播】信息查询
     */
    @POST("/ipvoice/direct/request")
    Observable<CallBackCallid> getCallLiveInfo(@Body HashMap<String, Object> map);


}
