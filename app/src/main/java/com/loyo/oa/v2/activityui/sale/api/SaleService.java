package com.loyo.oa.v2.activityui.sale.api;

import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleField;
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleOpportunity;
import com.loyo.oa.v2.activityui.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;

/**
 * 销售机会，网络服务
 * Created by jie on 16/12/22.
 */

public class SaleService {

    //获取 销售机会的动态字段
    public static Observable<ArrayList<ContactLeftExtras>> getSaleField() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleField()
                        .compose(RetrofitAdapterFactory.<ArrayList<ContactLeftExtras>>compatApplySchedulers());
    }

    //获取 销售阶段
    public static Observable<ArrayList<SaleStage>> getSaleStage() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleStage()
                        .compose(RetrofitAdapterFactory.<ArrayList<SaleStage>>compatApplySchedulers());
    }

    //获取 销售 产品【product】 机会类型【change_type】 机会来源【changeSource】
    public static Observable<SaleField> getSaleSystem(HashMap<String, String> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleSystem(params)
                        .compose(RetrofitAdapterFactory.<SaleField>compatApplySchedulers());
    }
    //获取 我的销售机会列表
    public static Observable<SaleList> getSaleMyList(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleMyList(params)
                        .compose(RetrofitAdapterFactory.<SaleList>compatApplySchedulers());
    }

    //获取 团队销售机会列表
    public static Observable<SaleList> getSaleTeamList(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleTeamList(params)
                        .compose(RetrofitAdapterFactory.<SaleList>compatApplySchedulers());
    }

    //获取 销售机会详情
    public static Observable<SaleDetails> getSaleDetails(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getSaleDetails(id)
                        .compose(RetrofitAdapterFactory.<SaleDetails>compatApplySchedulers());
    }

    //创建 销售机会
    public static Observable<SaleOpportunity> addSaleOpportunity(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .addSaleOpportunity(params)
                        .compose(RetrofitAdapterFactory.<SaleOpportunity>compatApplySchedulers());
    }

    //编辑 销售机会
    public static Observable<SaleOpportunity> updateSaleOpportunity(HashMap<String, Object> map, String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .updateSaleOpportunity(map,id)
                        .compose(RetrofitAdapterFactory.<SaleOpportunity>compatApplySchedulers());
    }

    //删除 销售机会
    public static Observable<SaleDetails> deleteSaleOpportunity(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .deleteSaleOpportunity(id)
                        .compose(RetrofitAdapterFactory.<SaleDetails>compatApplySchedulers());
    }

    //删除详情下的意向产品
    public static Observable<SaleProductEdit> deleteSaleProduct(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .deleteSaleProduct(map)
                        .compose(RetrofitAdapterFactory.<SaleProductEdit>compatApplySchedulers());
    }

    //新增详情下的意向产品
    public static Observable<SaleProductEdit> addSaleProduct(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .addSaleProduct(map)
                        .compose(RetrofitAdapterFactory.<SaleProductEdit>compatApplySchedulers());
    }

    //编辑详情下的意向产品
    public static Observable<SaleProductEdit> editSaleProduct(HashMap<String, Object> map,String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .editSaleProduct(map,id)
                        .compose(RetrofitAdapterFactory.<SaleProductEdit>compatApplySchedulers());
    }

    //编辑详情下的销售阶段
    public static Observable<SaleProductEdit> editSaleStage(HashMap<String, Object> map,String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .editSaleStage(map,id)
                        .compose(RetrofitAdapterFactory.<SaleProductEdit>compatApplySchedulers());
    }

    // 获取 客户 下的销售机会
    public static Observable<PaginationX<SaleRecord>> getCustomerSale(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISale.class)
                        .getCustomerSale(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<SaleRecord>>compatApplySchedulers());
    }
}