package com.loyo.oa.v2.point;

import android.telecom.Call;

import com.loyo.oa.v2.activity.sale.bean.SaleDetails;
import com.loyo.oa.v2.activity.sale.bean.SaleFild;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.activity.sale.bean.SaleOpportunityAdd;
import com.loyo.oa.v2.activity.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activity.sale.bean.SaleStage;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamList;
import com.loyo.oa.v2.beans.ContactLeftExtras;

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
    @GET("/salestage")
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
    void getSaleMyList(@QueryMap HashMap<String, Object> map, Callback<SaleMyList> callback);

    /**
     * 获取 我的销售机会列表
     */
    @GET("/chance/team")
    void getSaleTeamList(@QueryMap HashMap<String, Object> map, Callback<SaleTeamList> callback);

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
     * */
    @PUT("/chance")
    void updateSaleOpportunity(@Body HashMap<String,Object> map,Callback<SaleOpportunityAdd> callback);

    /**
     * 删除 销售机会
     * */
    @DELETE("/chance/{id}")
    void deleteSaleOpportunity(@Path("id") String id,Callback<SaleDetails> callback);

    /**
     * 删除详情下的意向产品
     * */
    @DELETE("/chance/pro")
    void deleteSaleProduct(@QueryMap HashMap<String,Object> map,Callback<SaleProductEdit> callback);

    /**
     * 新增详情下的意向产品
     * */
    @POST("/chance/addPro")
    void addSaleProduct(@Body HashMap<String,Object> map,Callback<SaleProductEdit> callback);

    /**
     * 编辑详情下的意向产品
     * */
    @POST("/chance/updatePro")
    void editSaleProduct(@Body HashMap<String,Object> map,Callback<SaleProductEdit> callback);

}
