package com.loyo.oa.v2.order.api;

import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

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
                        .create(I2Order.class)
                        .getOrderMyList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<OrderListItem>>compatApplySchedulers());
    }

    public static
    Observable<PaginationX<OrderListItem>> getOrderTeamList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .getOrderTeamList(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<OrderListItem>>compatApplySchedulers());
    }

    public static
    Observable<OrderAdd> editOrder(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .editOrder(id, map)
                        .compose(RetrofitAdapterFactory.<OrderAdd>compatApplySchedulers());
    }

    public static
    Observable<OrderAdd> addOrder(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .addOrder(map)
                        .compose(RetrofitAdapterFactory.<OrderAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimateAdd> addPayEstimate(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .addPayEstimate(map)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimateAdd> editPayEstimate(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .editPayEstimate(id, map)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> addPlanEstimate(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .addPlanEstimate(map)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> editPlanEsstimate(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .editPlanEsstimate(id, map)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<OrderDetail> getSaleDetails(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .getSaleDetails(id)
                        .compose(RetrofitAdapterFactory.<OrderDetail>compatApplySchedulers());
    }

    public static
    Observable<Object> deleteOrder(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .deleteOrder(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    public static
    Observable<Object> terminationOrder(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .terminationOrder(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }

    public static
    Observable<EstimateAdd> deletePayEstimate(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .deletePayEstimate(id)
                        .compose(RetrofitAdapterFactory.<EstimateAdd>compatApplySchedulers());
    }

    public static
    Observable<EstimateList> getPayEstimate(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .getPayEstimate(id)
                        .compose(RetrofitAdapterFactory.<EstimateList>compatApplySchedulers());
    }

    public static
    Observable<EstimatePlanAdd> deletePlanEsstimateList(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .deletePlanEsstimateList(id)
                        .compose(RetrofitAdapterFactory.<EstimatePlanAdd>compatApplySchedulers());
    }

    public static
    Observable<ArrayList<PlanEstimateList>> getPlanEstimateList(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.API_URL_CUSTOMER())
                        .create(I2Order.class)
                        .getPlanEstimateList(map)
                        .compose(RetrofitAdapterFactory.<ArrayList<PlanEstimateList>>compatApplySchedulers());
    }

}
