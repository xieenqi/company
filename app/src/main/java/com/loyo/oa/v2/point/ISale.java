package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activity.sale.salebens.SaleFild;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * 销售机会相关 接口
 * Created by xeq on 16/5/18.
 */
public interface ISale {
    /**
     * 获取 销售机会的动态字段
     */
    @GET("/chance/filed ")
    void getSaleFild(Callback<ArrayList<SaleFild>> callback);
}
