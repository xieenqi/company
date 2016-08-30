package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.clue.bean.ClueDetailWrapper;
import com.loyo.oa.v2.activityui.clue.bean.ClueList;
import com.loyo.oa.v2.activityui.clue.bean.SourcesData;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 线索相关接口 相关
 * Created by xeq on 16/8/22.
 */
public interface IClue {

    /**
     * 获取 线索来源
     *
     * @param callback /saleslead/source
     */
    @GET("/saleslead/source")
    void getSource(Callback<SourcesData> callback);

    /**
     * 我的线索列表
     */
    @GET("/salesleads/mobile")
    void getMyCluelist(@QueryMap HashMap<String, Object> map, Callback<ClueList> callback);

    /**
     * 团队线索列表
     */
    @GET("/salesleads/mobile/team")
    void getTeamCluelist(@QueryMap HashMap<String, Object> mao, Callback<ClueList> callback);


    /**
     * 新建线索 表单传输
     *
     * @param body
     * @param callback
     */
    @POST("/salesleads")
    void addClue(@Body Map<String, Object> body, Callback<ClueDetailWrapper> callback);


    /**
     * 获取 线索详情
     */
    @GET("/salesleads/mobile/{id}")
    void getClueDetail(@Path("id") String id, Callback<ClueDetailWrapper> callback);

    /**
     * 编辑 线索
     */
    @PUT("/salesleads/{id}")
    void editClue(@Path("id") String id, @Body HashMap<String, Object> map, Callback<Object> callback);

    /**
     * 获取跟进
     * @param map
     * @param cb
     */

    @GET("/saleactivity/saleslead")
    void getSaleactivity(@QueryMap HashMap<String, Object> map, retrofit.Callback<PaginationX<SaleActivity>> cb);


    /**
     * 新建跟进
     * @param map
     * @param cb
     */
    @POST("/saleactivity/sealslead")
    void addSaleactivity(@Body HashMap<String, Object> map, Callback<SaleActivity> cb);

    /**
     * 删除
     * @param map
     * @param callback
     */
    @PUT("/salesleads/batch/delete")
    void deleteClue(@Body HashMap<String, Object> map, Callback<Object> callback);


    /**
     * 转移 线索/salesleads/batch/transfer
     */
    @PUT("/salesleads/batch/transfer")
    void transferClue(@Body HashMap<String, Object> map, Callback<Object> callback);
}
