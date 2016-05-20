package com.loyo.oa.v2.point;

import android.telecom.Call;

import com.loyo.oa.v2.activity.sale.bean.SaleFild;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.activity.sale.bean.SaleStage;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;
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
    void getSaleFild(Callback<ArrayList<SaleFild>> callback);

    /**
     * 获取 销售阶段
     */
    @GET("/salestage")
    void getSaleStage(Callback<ArrayList<SaleStage>> callback);

    /**
     * 获取 销售 产品【product】 机会类型【change_type】 机会来源【changeSource】
     */
    @GET("/chance/filed/name")
    void getSaleStage111(@QueryMap HashMap<String, String> map, Callback<ArrayList<SaleFild>> callback);

    /**
     * 获取 我的销售机会列表
     * */
    @GET("/chance/self")
    void getSaleMyList(@QueryMap HashMap<String,Object> map,Callback<SaleMyList> callback);

}
