package com.loyo.oa.v2.activityui.wfinstance.api;

import android.util.Log;

import com.loyo.oa.v2.activityui.clue.api.IClue;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.MySubmitWflnstance;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfTemplate;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 流程审批 网络请求
 * Created by jie on 16/12/20.
 */

public class WfinstanceService {

    //获取审批类型
    public static Observable<PaginationX<BizForm>> getWfBizForms(HashMap<String, Object> params) {
        return RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWfinstance.class)
                        .getWfBizForms(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<BizForm>>compatApplySchedulers());
    }

    //获取审批类型的详情
    public static Observable<BizForm> getWfBizForm(String id) {
        return RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWfinstance.class)
                        .getWfBizForm(id)
                        .compose(RetrofitAdapterFactory.<BizForm>compatApplySchedulers());
    }

    //根据审批类型id获取对应的流程
    public static Observable<ArrayList<WfTemplate>> getWfTemplate(String id) {
        return RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL())
                        .create(IWfinstance.class)
                        .getWfTemplate(id)
                        .compose(RetrofitAdapterFactory.<ArrayList<WfTemplate>>compatApplySchedulers());
    }

    //新增审批
    public static Observable<WfInstance> addWfInstance(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .addWfInstance(map)
                .compose(RetrofitAdapterFactory.<WfInstance>compatApplySchedulers());
    }

    //编辑审批
    public static Observable<WfInstance> editWfInstance(String id,HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .editWfInstance(id,map)
                .compose(RetrofitAdapterFactory.<WfInstance>compatApplySchedulers());
    }

    //审批
    public static Observable<WfInstance> doWfInstance(String id,HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .doWfInstance(id,map)
                .compose(RetrofitAdapterFactory.<WfInstance>compatApplySchedulers());
    }


    //获取审批列表
    public static Observable<PaginationX<WfInstance>> getWfInstances(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .getWfInstances(map)
                .compose(RetrofitAdapterFactory.<PaginationX<WfInstance>>compatApplySchedulers());
    }

    // 获取审批列表(v2.2 精简版)
    public static Observable<PaginationX<WfInstanceRecord>> getWfInstancesData(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL()+ FinalVariables.wfinstance)
                .create(IWfinstance.class)
                .getWfInstancesData(map)
                .compose(RetrofitAdapterFactory.<PaginationX<WfInstanceRecord>>compatApplySchedulers());
    }

    // 获取我提交的 审批 列表数据
    public static Observable<MySubmitWflnstance> getSubmitWfInstancesList(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL()+ FinalVariables.wfinstance)
                .create(IWfinstance.class)
                .getSubmitWfInstancesList(map)
                .compose(RetrofitAdapterFactory.<MySubmitWflnstance>compatApplySchedulers());
    }

    // 获取 我审批的 审批 列表数据
    public static Observable<MySubmitWflnstance> getApproveWfInstancesList(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL()+FinalVariables.wfinstance)
                .create(IWfinstance.class)
                .getApproveWfInstancesList(map)
                .compose(RetrofitAdapterFactory.<MySubmitWflnstance>compatApplySchedulers());
    }

    // 获取审批详情
    public static Observable<WfInstance> getWfInstance(String id) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .getWfInstance(id)
                .compose(RetrofitAdapterFactory.<WfInstance>compatApplySchedulers());
    }

    // 删除审批详情
    public static Observable<WfInstance> deleteWfinstance(String id) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL())
                .create(IWfinstance.class)
                .deleteWfinstance(id)
                .compose(RetrofitAdapterFactory.<WfInstance>compatApplySchedulers());
    }
}
