package com.loyo.oa.v2.customermanagement.api;

import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerExtraData;
import com.loyo.oa.v2.activityui.customer.model.CustomerRepeatList;
import com.loyo.oa.v2.activityui.customer.model.Industry;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.model.NearCount;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.customer.model.Product;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.activityui.wfinstance.bean.ApprovalItemModel;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit.http.Body;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/15.
 */

public class CustomerService {
    public static Observable<PaginationX<Customer>> getMyCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_RESPON)
                        .create(ICustomer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<NearCount> getNearbySelfCustomerCount(String position) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_SELF)
                        .create(ICustomer.class)
                        .getNearbySelfCustomerCount(position)
                        .compose(RetrofitAdapterFactory.<NearCount>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getTeamCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_TEAM)
                        .create(ICustomer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<NearCount> getNearbyTeamCustomerCount(String position) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_TEAM)
                        .create(ICustomer.class)
                        .getNearbySelfCustomerCount(position)
                        .compose(RetrofitAdapterFactory.<NearCount>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getMemberCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_MEMBER)
                        .create(ICustomer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<PaginationX<Customer>> getDumpedCustomers(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/FinalVariables.QUERY_CUSTOMERS_PUBLIC)
                        .create(ICustomer.class)
                        .getCustomers(params)
                        .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers());
    }

    public static Observable<Customer> pickInCustomer(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .pickInCustomer(id)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<ArrayList<ContactLeftExtras>> getAddCustomerJur(Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getAddCustomerJur(map)
                        .compose(RetrofitAdapterFactory.<ArrayList<ContactLeftExtras>>compatApplySchedulers());
    }

    public static Observable<Customer> addNewCustomer(Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .addNewCustomer(map)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<ArrayList<ContactLeftExtras>> getContactsField() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getContactsField()
                        .compose(RetrofitAdapterFactory.<ArrayList<ContactLeftExtras>>compatApplySchedulers());
    }

    public static Observable<Customer> getCustomerContacts(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getCustomerContacts(id)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<CallBackCallid> requestCallBack(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .requestCallBack(map)
                        .compose(RetrofitAdapterFactory.<CallBackCallid>compatApplySchedulers());
    }

    public static Observable<Contact> deleteContact(String id, String consid) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .deleteContact(id, consid)
                        .compose(RetrofitAdapterFactory.<Contact>compatApplySchedulers());
    }

    public static Observable<Contact> setDefaultContact(String id, String consid) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .setDefaultContact(id, consid)
                        .compose(RetrofitAdapterFactory.<Contact>compatApplySchedulers());
    }

    public static Observable<Contact> addContact(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .addContact(id, map)
                        .compose(RetrofitAdapterFactory.<Contact>compatApplySchedulers());
    }

    public static Observable<Contact> updateContact(String id, String consid, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .updateContact(id, consid, map)
                        .compose(RetrofitAdapterFactory.<Contact>compatApplySchedulers());
    }

    public static Observable<Customer> getCustomerDetailById(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getCustomerById(id)
                        .compose(RetrofitAdapterFactory.<Customer>applySchedulers());
    }

    public static Observable<MembersRoot> getMembersRoot() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getMembersRoot()
                        .compose(RetrofitAdapterFactory.<MembersRoot>compatApplySchedulers());

    }

    public static Observable<Customer> deleteCustomer(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .delete(id)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<Customer> dumpCustomer(@Path("id") String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .toPublic(id)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<BaseResponse<Object>> dumpCustomer(@Path("id") String id, @Body HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .toPublic(id, map)
                        .compose(RetrofitAdapterFactory.<BaseResponse<Object>>compatApplySchedulers());
    }

    public static Observable<ArrayList<CustomerExtraData>> getCustomerDynamic(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getDynamic(map)
                        .compose(RetrofitAdapterFactory.<ArrayList<CustomerExtraData>>compatApplySchedulers());
    }

    public static Observable<Customer> updateCustomer(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .updateCustomer(id, map)
                        .compose(RetrofitAdapterFactory.<Customer>compatApplySchedulers());
    }

    public static Observable<ArrayList<Tag>> getCustomerTags() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .GetTags()
                        .compose(RetrofitAdapterFactory.<ArrayList<Tag>>compatApplySchedulers());
    }

    public static Observable<Contact> setCusLabel(String id, ArrayList<NewTag> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .setCusLabel(id, map)
                        .compose(RetrofitAdapterFactory.<Contact>compatApplySchedulers());
    }


    public static Observable<ArrayList<CommonTag>> getLoseReasons() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getLoseReasons()
                        .compose(RetrofitAdapterFactory.<ArrayList<CommonTag>>compatApplySchedulers());
    }

    public static Observable<ArrayList<CommonTag>> getSaleactivitytypes() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getSaleactivitytypes()
                        .compose(RetrofitAdapterFactory.<ArrayList<CommonTag>>compatApplySchedulers());
    }

    public static Observable<SaleActivity> addSaleactivity(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .addSaleactivity(map)
                        .compose(RetrofitAdapterFactory.<SaleActivity>compatApplySchedulers());

    }

    public static Observable<PaginationX<CustomerRepeatList>> getCustomerDuplicates(Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getSerachRepeat(map)
                        .compose(RetrofitAdapterFactory.<PaginationX<CustomerRepeatList>>compatApplySchedulers());

    }

    public static Observable<ArrayList<Product>> getProducts() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getProducts()
                        .compose(RetrofitAdapterFactory.<ArrayList<Product>>compatApplySchedulers());
    }

    public static Observable<String> cancelCallBack(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .cancelCallBack(id)
                        .compose(RetrofitAdapterFactory.<String>applySchedulers());
    }

    public static Observable<PaginationX<OrderListItem>> getCutomerOrder(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getCutomerOrder(id, map)
                        .compose(RetrofitAdapterFactory.<PaginationX<OrderListItem>>compatApplySchedulers());
    }

    /**
     * 获取客户关联的审批列表
     */
    public static Observable<PaginationX<ApprovalItemModel>> getRelatedApprovalList(HashMap<String, Object> map) {
        return RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/Config_project.API_URL() + FinalVariables.wfinstance)
                .create(ICustomer.class)
                .getRelatedApprovalList(map)
                .compose(RetrofitAdapterFactory.<PaginationX<ApprovalItemModel>>compatApplySchedulers());
    }

    public static Observable<SigninPictures> getSigninUploadPhotoConfig(@QueryMap Map<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getSetInfo(map)
                        .compose(RetrofitAdapterFactory.<SigninPictures>compatApplySchedulers());
    }

    public static Observable<LegWork> addSignIn(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .addSignIn(map)
                        .compose(RetrofitAdapterFactory.<LegWork>compatApplySchedulers());

    }

    public static Observable<ArrayList<SaleStage>> getSaleStges() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getSaleStges()
                        .compose(RetrofitAdapterFactory.<ArrayList<SaleStage>>compatApplySchedulers());
    }

    public static Observable<ArrayList<Industry>> getIndustry() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getIndustry()
                        .compose(RetrofitAdapterFactory.<ArrayList<Industry>>compatApplySchedulers());

    }

    /* 获取丢公海原因*/
    public static Observable<BaseResponse<ArrayList<SaleStage>>> getCommonReason() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ICustomer.class)
                        .getCoomonReason()
                        .compose(RetrofitAdapterFactory.<BaseResponse<ArrayList<SaleStage>>>compatApplySchedulers());

    }

}
