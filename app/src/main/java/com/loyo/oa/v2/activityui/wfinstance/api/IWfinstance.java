package com.loyo.oa.v2.activityui.wfinstance.api;

import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.MySubmitWflnstance;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfInstanceRecord;

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
import rx.Observable;

/**
 * 流程审批 服务器接口
 * Created by jie on 16/12/20.
 */

public interface IWfinstance {

    /**
     * 获取审批类型
     */
    @GET("/wfbizform/")
    Observable<PaginationX<BizForm>> getWfBizForms(@QueryMap HashMap<String, Object> map);


    /**
     * 获取审批类型的详情
     */
    @GET("/wfbizform/{id}")
    Observable<BizForm> getWfBizForm(@Path("id") String id);

    /**
     * 根据审批类型id获取对应的流程
     */
    @GET("/wftpl/bizformId/{id}")
    Observable<ArrayList<WfTemplate>> getWfTemplate(@Path("id") String id);

    /**
     * 新增审批
     */
    @POST("/wfinstance/")
    Observable<WfInstance> addWfInstance(@Body HashMap<String, Object> map);

    /**
     * 编辑审批
     */
    @PUT("/wfinstance/{id}")
    Observable<WfInstance> editWfInstance(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 审批
     */
    @PUT("/wfinstance/{id}/approve/")
    Observable<WfInstance> doWfInstance(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 获取审批列表
     */
    @GET("/")
    Observable<PaginationX<WfInstance>> getWfInstances(@QueryMap HashMap<String, Object> map);

    /**
     * 获取审批列表(v2.2 精简版)
     */
    @GET("/mobile/simplify")
    Observable<PaginationX<WfInstanceRecord>> getWfInstancesData(@QueryMap HashMap<String, Object> map);


    /**
     * 获取我提交的 审批 列表数据
     */
    @GET("/new/simplify")
    Observable<MySubmitWflnstance> getSubmitWfInstancesList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取 我审批的 审批 列表数据
     */
    @GET("/approve/mobile")
    Observable<MySubmitWflnstance> getApproveWfInstancesList(@QueryMap HashMap<String, Object> map);

    /**
     * 获取审批详情
     */
    @GET("/wfinstance/{id}")
    Observable<WfInstance> getWfInstance(@Path("id") String id);

    /**
     * 删除审批详情
     */
    @DELETE("/wfinstance/{id}")
    Observable<WfInstance> deleteWfinstance(@Path("id") String id);

}
