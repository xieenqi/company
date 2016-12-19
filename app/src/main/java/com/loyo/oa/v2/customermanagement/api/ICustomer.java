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
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.beans.CustomerFollowUpModel;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by pj on 15/5/28.
 *
 * Updated by EthanGong on 2016/12/15.
 */

public interface ICustomer {

    /** 我负责的客户 */
    @GET("/")
    Observable<PaginationX<Customer>> getCustomers(@QueryMap Map<String, Object> params);

    /** 我负责的客户 附近数目 */
    @GET("/")
    Observable<NearCount> getNearbySelfCustomerCount(@Query("position") String position);

    @GET("/customers")
    Observable<PaginationX<Customer>> Query(@QueryMap Map<String, Object> params);

    /**
     * name "客户名称"	string
     * loc		"客户位置信息"	models.customer.Location
     * industry		"行业"	models.Industry	form
     * regional		"地区"	models.customer.Regional
     * tags		false "标签"
     * ModelModel Schema<BR/>
     * CustomerTag <BR/>
     * summary		"简介"	string
     * owner		"项目负责人"	tmodels.Member
     * members		false "项目成员"	[]
     * extDatas		false "自定义字段值"	[]
     *
     * @param id
     * @param map
     */
    @PUT("/customer/{id}")
    Observable<Customer> updateCustomer(@Path("id") String id, @Body HashMap<String, Object> map);

    /**
     * 获取客户标签
     */
    @GET("/customer/tags")
    Observable<ArrayList<Tag>> GetTags();

    /**
     * 获取客户详情
     * /customer/{id}  改之前的接口 20160613
     *
     * @param id       customer/mobile/574eb3ef526f155720edde30?_=1465786248655
     *
     * Updated by Ethan 2016-11-30
     *
     * @param id       customer/new/574eb3ef526f155720edde30?_=1465786248655
     *
     */
    @GET("/customer/new/{id}")
    Observable<BaseResponse<Customer>> getCustomerById(@Path("id") String id);

    @GET("/properties/")
    Observable<ArrayList<CustomerExtraData>> getDynamic(@QueryMap HashMap<String, Object> map);

    /**
     * 获取参与人权限
     */
    @GET("/config/?key=cust_member_canedit")
    Observable<MembersRoot> getMembersRoot();

    /**
     * 丢入公海
     *
     * @param id
     */
    @PUT("/customer/{id}/sea")
    Observable<Customer> toPublic(@Path("id") String id);

    /**
     * 从公海挑入
     *
     * @param id
     */
    @PUT("/customer/{id}/pick")
//<<<<<<< HEAD:app/src/main/java/com/loyo/oa/v2/customermanagement/api/ICustomer.java
    Observable<Customer> pickInCustomer(@Path("id") String id);
//=======
//    void pickedIn(@Path("id") String id, Callback<BaseBean> callback);
//>>>>>>> develop:app/src/main/java/com/loyo/oa/v2/point/ICustomer.java

    /**
     * 客户详情,编辑跟进标签
     */
    @PUT("/customer/tag/{id}")
    Observable<Contact> setCusLabel(@Path("id") String id, @Body ArrayList<NewTag> map);

    /**
     * 删除客户
     *
     * @param id
     */
    @DELETE("/customer/{id}")
    Observable<Customer> delete(@Path("id") String id);

    /**
     * 删除客户联系人
     *
     * @param id
     * @param consid
     */
    @DELETE("/customer/{id}/contact/{consid}")
    Observable<Contact> deleteContact(@Path("id") String id, @Path("consid") String consid);

    /**
     * 设置默认联系人
     *
     * @param id
     * @param consid
     */
    @PUT("/customer/{id}/defaultcontact/{consid}")
    Observable<Contact> setDefaultContact(@Path("id") String id, @Path("consid") String consid);

    /**
     * 新增联系人
     *
     * @param id
     * @param map
     */
    @POST("/customer/contact/{id}")
    Observable<Contact> addContact(@Path("id") String id, @Body HashMap<String, Object> map);

    @PUT("/customer/{id}/contact/{consid}")
    Observable<Contact> updateContact(@Path("id") String id, @Path("consid") String consid, @Body HashMap<String, Object> map);


    @GET("/saleactivity/{cusId}")
    Observable<PaginationX<CustomerFollowUpModel>> getSaleactivity(@Path("cusId") String cusId, @QueryMap HashMap<String, Object> map);

    @GET("/saleactivitytype/")
    Observable<ArrayList<CommonTag>> getSaleactivitytypes();

    /*客户 写跟进
     * @param map
     */
    @POST("/saleactivity/")
    Observable<SaleActivity> addSaleactivity(@Body HashMap<String, Object> map);

    @GET("/losereason/")
    Observable<ArrayList<CommonTag>> getLoseReasons();


    @GET("/product")
    Observable<ArrayList<Product>> getProducts();

    @GET("/salestage")
    Observable<SaleStage> getSaleStges();


    /**
     * gpsInfo		"gps信息(火星坐标),例106.12313143,68.1231124--经度,纬度"	form	string<BR/>
     * address		"gps的地址信息"	form	string<BR/>
     * customerId		"客户Id"	form	string<BR/>
     * memo		"备注"	form	string<BR/>
     * mentionedUserIds		"抄送给哪些人"	form	string<BR/>
     * mentionedDeptIds		"抄送给哪些部门"	form	string<BR/>
     * mentionedGroupIds		"抄送给哪些群组"	form	string<BR/>
     * attachmentUUId		"附件的 UUId"	form	string<BR/>
     *
     * @param map
     */
    @POST("/visit")
    Observable<LegWork> addSignIn(@Body HashMap<String, Object> map);

    @GET("/visit/bycustId")
    Observable<PaginationX<LegWork>> getLegworks(@QueryMap HashMap<String, Object> map);


    @GET("/visit/{id}")
    Observable<LegWork> getLegwork(@Path("id") String id);

    @GET("/customer/industry")
    Observable<ArrayList<Industry>> getIndustry();

    /**
     * 新建客户，查重
     */
    @GET("/customer/search")
    Observable<PaginationX<CustomerRepeatList>> getSerachRepeat(@QueryMap Map<String, Object> params);

    /**
     * 获取客户新建联系人的动态字段
     *
     */
    @GET("/contact/fields")
    Observable<ArrayList<ContactLeftExtras>> getContactsField();

    /**
     * 获取客户联系人的详细数据
     */
    @GET("/contact/{customerId}")
    Observable<Customer> getCustomerContacts(@Path("customerId") String id);

    @POST("/customer/")
    Observable<Customer> addNewCustomer(@Body Map<String, Object> map);

    /**
     * 根据 key 获取后台设置信息
     * ?key=need_pictures_switcher
     *
     * @param map
     */
    @GET("/config")
    Observable<SigninPictures> getSetInfo(@QueryMap Map<String, Object> map);

    /**
     * 新建客户,后台权限
     */
    @GET("/properties")
    Observable<ArrayList<ContactLeftExtras>> getAddCustomerJur(@QueryMap Map<String, Object> map);

    /**
     * 获取 客户 的订单
     *
     */
    @GET("/order/cus/{id}")
    Observable<PaginationX<OrderListItem>> getCutomerOrder(@Path("id") String id, @QueryMap HashMap<String, Object> map);
    /**
     * 通知服务器请求回拨
     */
    @POST("/ipvoice/request")
    Observable<CallBackCallid> requestCallBack(@Body HashMap<String, Object> map);

    /**
     * 通知服务器取消回拨
     */
    @GET("/ipvoice/callCancel/{callLogId}")
    Observable<BaseResponse<String>> cancelCallBack(@Path("callLogId") String id);
}
