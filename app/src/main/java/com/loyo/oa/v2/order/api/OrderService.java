package com.loyo.oa.v2.order.api;

import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.activityui.order.bean.ProcessItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.Path;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/21.
 */

public class OrderService {

    public static
    Observable<PaginationX<OrderListItem>> getOrderMyList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getOrderMyList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<OrderListItem>>compatApplySchedulers());
    }

    public static
    Observable<PaginationX<OrderListItem>> getOrderTeamList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getOrderTeamList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<OrderListItem>>compatApplySchedulers());
    }

    public static
    Observable<OrderAdd> editOrder(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .editOrder(id, map)
                        .compose(RetrofitAdapterFactory.<OrderAdd>applySchedulers());
    }

    public static
    Observable<OrderAdd> addOrder(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .addOrder(map)
                        .compose(RetrofitAdapterFactory.<OrderAdd>applySchedulers());
    }

    public static
    Observable<EstimateAdd> addPayEstimate(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .addPayEstimate(map)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimateAdd> editPayEstimate(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .editPayEstimate(id, map)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> addPlanEstimate(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .addPlanEstimate(map)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> editPlanEsstimate(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .editPlanEsstimate(id, map)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<OrderDetail> getSaleDetails(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getSaleDetails(id, map)
                        .compose(RetrofitAdapterFactory.<OrderDetail>compatApplySchedulers());
    }

    public static
    Observable<Object> deleteOrder(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .deleteOrder(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    public static
    Observable<Object> terminationOrder(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .terminationOrder(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    public static
    Observable<EstimateAdd> deletePayEstimate(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .deletePayEstimate(id)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimateList> getPayEstimate(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getPayEstimate(id)
                        .compose(RetrofitAdapterFactory.<EstimateList>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> deletePlanEsstimateList(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .deletePlanEsstimateList(id)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<ArrayList<PlanEstimateList>> getPlanEstimateList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getPlanEstimateList(map)
                        .compose(RetrofitAdapterFactory.<ArrayList<PlanEstimateList>>compatApplySchedulers());
    }

    public static
    Observable<Boolean> getTerminateProcessConfig() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getTerminateProcessConfig()
                        .compose(RetrofitAdapterFactory.<Boolean>applySchedulers());
    }

    public static
    Observable<ArrayList<ProcessItem>> getTerminateProcessList() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .getTerminateProcessList()
                        .compose(RetrofitAdapterFactory.<ArrayList<ProcessItem>>applySchedulers());
    }

    public static
    Observable<Object> terminationOrderWithProcess(@Path("id") String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(IOrder.class)
                        .terminationOrderWithProcess(id, map)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

}
