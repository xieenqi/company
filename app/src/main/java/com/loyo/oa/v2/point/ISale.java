package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleFild;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleOpportunityAdd;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamList;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 销售机会相关 接口
 * Created by xeq on 16/5/18.
 */
public interface ISale {
    /**
     * 获取 销售机会的动态字段
     */
    @GET("/chance/filed")
    void getSaleFild(Callback<ArrayList<ContactLeftExtras>> callback);

    /**
     * 获取 销售阶段
     */
    @GET("/salestage/alluse")
// /salestage
    void getSaleStage(Callback<ArrayList<SaleStage>> callback);

    /**
     * 获取 销售 产品【product】 机会类型【change_type】 机会来源【changeSource】
     */
    @GET("/chance/filed/name")
    void getSaleSystem(@QueryMap HashMap<String, String> map, Callback<SaleFild> callback);

    /**
     * 获取 我的销售机会列表
     */
    @GET("/chance/self")
    void getSaleMyList(@QueryMap HashMap<String, Object> map, Callback<SaleList> callback);

    /**
     * 获取 我的销售机会列表
     */
    @GET("/chance/team")
    void getSaleTeamList(@QueryMap HashMap<String, Object> map, Callback<SaleList> callback);

    /**
     * 获取 销售机会详情
     */
    @GET("/chance/{id}")
    void getSaleDetails(@Path("id") String id, Callback<SaleDetails> callback);

    /**
     * 创建 销售机会
     */
    @POST("/chance")
    void addSaleOpportunity(@Body HashMap<String, Object> body, Callback<SaleOpportunityAdd> callback);

    /**
     * 编辑 销售机会
     */
    @PUT("/chance/{id}")
    void updateSaleOpportunity(@Body HashMap<String, Object> map, @Path("id") String id, Callback<SaleOpportunityAdd> callback);

    /**
     * 删除 销售机会
     */
    @DELETE("/chance/{id}")
    void deleteSaleOpportunity(@Path("id") String id, Callback<SaleDetails> callback);

    /**
     * 删除详情下的意向产品
     */
    @DELETE("/chance/pro")
    void deleteSaleProduct(@QueryMap HashMap<String, Object> map, Callback<SaleProductEdit> callback);

    /**
     * 新增详情下的意向产品
     */
    @POST("/chance/addPro")
    void addSaleProduct(@Body HashMap<String, Object> map, Callback<SaleProductEdit> callback);

    /**
     * 编辑详情下的意向产品
     */
    @PUT("/chance/updatePro/{id}")
    void editSaleProduct(@Body HashMap<String, Object> map, @Path("id") String id, Callback<SaleProductEdit> callback);

    /**
     * 编辑详情下的销售阶段
     */
    @PUT("/chance/updateStage/{id}")
    void editSaleStage(@Body HashMap<String, Object> map, @Path("id") String id, Callback<SaleProductEdit> callback);

    /**
     * 获取 客户 下的销售机会
     */
    @GET("/chance/allSale")
    void getCustomerSale(@QueryMap HashMap<String, Object> map, Callback<PaginationX<SaleRecord>> callback);


//    http://192.168.31.155:8070/api/v2/chance/allSale?customerId=57456c20526f15667c541556&pageIndex=1&pageSize=10
}
