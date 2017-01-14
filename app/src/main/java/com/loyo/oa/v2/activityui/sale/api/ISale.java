package com.loyo.oa.v2.activityui.sale.api;

import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleField;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleOpportunity;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 销售机会，网络接口
 * Created by jie on 16/12/22.
 */

public interface ISale {
    /**
     * 获取 销售机会的动态字段
     */
    @GET("/chance/filed")
    Observable<ArrayList<ContactLeftExtras>> getSaleField();

    /**
     * 获取 销售阶段
     */
    @GET("/salestage/alluse")
// /salestage
    Observable<ArrayList<SaleStage>> getSaleStage();

    /**
     * 获取 销售 产品【product】 机会类型【change_type】 机会来源【changeSource】
     */
    @GET("/chance/filed/name")
    Observable<SaleField> getSaleSystem(@QueryMap HashMap<String, String> map);

    /**
     * 获取 我的销售机会列表
     */
    @GET("/chance/self")
    Observable<PaginationX<SaleRecord>> getSaleMyList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 团队销售机会列表
     */
    @GET("/chance/team")
    Observable<PaginationX<SaleRecord>> getSaleTeamList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 销售机会详情
     */
    @GET("/chance/{id}")
    Observable<SaleDetails> getSaleDetails(@Path("id") String id);

    /**
     * 创建 销售机会
     */
    @POST("/chance")
    Observable<SaleOpportunity> addSaleOpportunity(@Body HashMap<String, Object> body);

    /**
     * 编辑 销售机会
     */
    @PUT("/chance/{id}")
    Observable<SaleOpportunity> updateSaleOpportunity(@Body HashMap<String, Object> map, @Path("id") String id);

    /**
     * 删除 销售机会
     */
    @DELETE("/chance/{id}")
    Observable<SaleDetails> deleteSaleOpportunity(@Path("id") String id);

    /**
     * 删除详情下的意向产品
     */
    @DELETE("/chance/pro")
    Observable<SaleProductEdit> deleteSaleProduct(@QueryMap HashMap<String, Object> map);

    /**
     * 新增详情下的意向产品
     */
    @POST("/chance/addPro")
    Observable<SaleProductEdit> addSaleProduct(@Body HashMap<String, Object> map);

    /**
     * 编辑详情下的意向产品
     */
    @PUT("/chance/updatePro/{id}")
    Observable<SaleProductEdit> editSaleProduct(@Body HashMap<String, Object> map, @Path("id") String id);

    /**
     * 编辑详情下的销售阶段
     */
    @PUT("/chance/updateStage/{id}")
    Observable<SaleProductEdit> editSaleStage(@Body HashMap<String, Object> map, @Path("id") String id);

    /**
     * 获取 客户 下的销售机会
     */
    @GET("/chance/allSale")
    Observable<PaginationX<SaleRecord>> getCustomerSale(@QueryMap HashMap<String, Object> map);

}
