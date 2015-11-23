package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.BizForm;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfTemplate;

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
 * com.loyo.oa.v2.point
 * 描述 :审批交互接口
 * 作者 : ykb
 * 时间 : 15/11/5.
 */
public interface IWfInstance {

    /**
     * 获取审批类型
     * @param map
     * @param callback
     */
    @GET("/wfbizform/")
    void getWfBizForms(@QueryMap HashMap<String,Object> map,Callback<PaginationX<BizForm>> callback);

    /**
     * 获取审批类型的详情
     * @param callback
     */
    @GET("/wfbizform/{id}")
    void getWfBizForm(@Path("id") String id,Callback<BizForm> callback);

    /**
     * 根据审批类型id获取对应的流程
     * @param id
     * @param callback
     */
    @GET("/wftpl/bizformId/{id}")
    void getWfTemplate(@Path("id") String id,Callback<ArrayList<WfTemplate>> callback);

    /**
     * 新增审批
     * @param map
     * @param callback
     */
    @POST("/wfinstance/")
    void addWfInstance(@Body HashMap<String,Object> map,Callback<WfInstance> callback);

    /**
     * 审批
     * @param id
     * @param map
     * @param callback
     */
    @PUT("/wfinstance/{id}/approve/")
    void doWfInstance(@Path("id") String id,@Body HashMap<String,Object> map,Callback<WfInstance> callback);

    /**
     * 获取审批列表
     * @param map
     * @param callback
     */
    @GET("/")
    void getWfInstances(@QueryMap HashMap<String,Object> map,Callback<PaginationX<WfInstance>> callback);

    /**
     * 获取审批详情
     * @param id
     * @param callback
     */
    @GET("/wfinstance/{id}")
    void getWfInstance(@Path("id") String id,Callback<WfInstance> callback);

    /**
     * 获取审批详情
     * @param id
     * @param callback
     */
    @DELETE("/wfinstance/{id}")
    void deleteWfinstance(@Path("id") String id,Callback<WfInstance> callback);


}
