package com.loyo.oa.v2.customermanagement.api;

import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/15.
 */

public class CustomerService {
    public static Observable<PaginationX<Customer>> getMyCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_RESPON)
                        .create(I2Customer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<NearCount> getNearbySelfCustomerCount(String position) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_SELF)
                        .create(I2Customer.class)
                        .getNearbySelfCustomerCount(position)
                        .compose(RetrofitAdapterFactory.<NearCount>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getTeamCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_TEAM)
                        .create(I2Customer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<NearCount> getNearbyTeamCustomerCount(String position) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_TEAM)
                        .create(I2Customer.class)
                        .getNearbySelfCustomerCount(position)
                        .compose(RetrofitAdapterFactory.<NearCount>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getMemberCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_MEMBER)
                        .create(I2Customer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getDumpedCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_PUBLIC)
                        .create(I2Customer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<Customer> pickInCustomer(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(I2Customer.class)
                        .pickInCustomer(id)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<ArrayList<ContactLeftExtras>> getAddCustomerJur(Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(I2Customer.class)
                        .getAddCustomerJur(map)
                        .compose(RetrofitAdapterFactory.<ArrayList<ContactLeftExtras>>compatApplySchedulers());
    }

    public static Observable<Customer> addNewCustomer(Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(I2Customer.class)
                        .addNewCustomer(map)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }
}
