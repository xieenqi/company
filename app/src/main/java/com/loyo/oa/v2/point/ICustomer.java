package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.bean.CustomerExtraData;
import com.loyo.oa.v2.activityui.customer.bean.CustomerRepeatList;
import com.loyo.oa.v2.activityui.customer.bean.Industry;
import com.loyo.oa.v2.activityui.customer.bean.MembersRoot;
import com.loyo.oa.v2.activityui.customer.bean.NearCount;
import com.loyo.oa.v2.activityui.customer.bean.Product;
import com.loyo.oa.v2.activityui.customer.bean.Tag;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.other.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by pj on 15/5/28.
 */
public interface ICustomer {

    @GET("/")
    void query(@QueryMap Map<String, Object> params, Callback<PaginationX<Customer>> cb);

    /**
     * 查询附近客户数量
     *
     * @param position
     * @param cb
     */
    @GET("/")
    void queryNearCount(@Query("position") String position, Callback<NearCount> cb);

    @GET("/customers")
    void Query(@QueryMap Map<String, Object> params, retrofit.Callback<PaginationX<Customer>> cb);

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
     * @param callback
     */
    @PUT("/customer/{id}")
    void updateCustomer(@Path("id") String id, @Body HashMap<String, Object> map, Callback<Customer> callback);

    /**
     * 获取客户标签
     *
     * @param cb
     */
    @GET("/customer/tags")
    void GetTags(retrofit.Callback<ArrayList<Tag>> cb);

    /**
     * 获取客户详情
     * /customer/{id}  改之前的接口 20160613
     *
     * @param id       customer/mobile/574eb3ef526f155720edde30?_=1465786248655
     * @param callback
     */
    @GET("/customer/mobile/{id}")
    void getCustomerById(@Path("id") String id, Callback<Customer> callback);

    @GET("/properties/")
    void getDynamic(@QueryMap HashMap<String, Object> map, Callback<ArrayList<CustomerExtraData>> callback);

    /**
     * 获取参与人权限
     */
    @GET("/config/?key=cust_member_canedit")
    void getMembersRoot(Callback<MembersRoot> callback);

    /**
     * 丢入公海
     *
     * @param id
     * @param callback
     */
    @PUT("/customer/{id}/sea")
    void toPublic(@Path("id") String id, Callback<Customer> callback);

    /**
     * 从公海挑入
     *
     * @param id
     * @param callback
     */
    @PUT("/customer/{id}/pick")
    void pickedIn(@Path("id") String id, Callback<Customer> callback);

    /**
     * 删除客户
     *
     * @param id
     * @param callback
     */
    @DELETE("/customer/{id}")
    void delete(@Path("id") String id, Callback<Customer> callback);

    /**
     * 删除客户联系人
     *
     * @param id
     * @param consid
     * @param callback
     */
    @DELETE("/customer/{id}/contact/{consid}")
    void deleteContact(@Path("id") String id, @Path("consid") String consid, Callback<Contact> callback);

    /**
     * 设置默认联系人
     *
     * @param id
     * @param consid
     * @param callback
     */
    @PUT("/customer/{id}/defaultcontact/{consid}")
    void setDefaultContact(@Path("id") String id, @Path("consid") String consid, Callback<Contact> callback);

    /**
     * 新增联系人
     *
     * @param id
     * @param map
     * @param callback
     */
    @POST("/customer/contact/{id}")
    void addContact(@Path("id") String id, @Body HashMap<String, Object> map, Callback<Contact> callback);

    @PUT("/customer/{id}/contact/{consid}")
    void updateContact(@Path("id") String id, @Path("consid") String consid, @Body HashMap<String, Object> map, Callback<Contact> callback);


    @GET("/saleactivity/{cusId}")
    void getSaleactivity(@Path("cusId") String cusId, @QueryMap HashMap<String, Object> map, retrofit.Callback<PaginationX<SaleActivity>> cb);

    @GET("/saleactivitytype/")
    void getSaleactivitytypes(Callback<ArrayList<CommonTag>> cb);

    /**
     * customerId		"客户id"	string	form<BR/>
     * content		"动态内容"	string	form<BR/>
     * remindAt		"提醒时间"	int64	form<BR/>
     * typeId		"动态类型id"	string	form<BR/>
     *
     * @param map
     * @param cb
     */
    @POST("/saleactivity/")
    void addSaleactivity(@Body HashMap<String, Object> map, Callback<SaleActivity> cb);

    @GET("/losereason/")
    void getLoseReasons(Callback<ArrayList<CommonTag>> cb);


    @GET("/product")
    void getProducts(Callback<ArrayList<Product>> cb);

    @GET("/salestage")
    void getSaleStges(Callback<ArrayList<SaleStage>> cb);


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
     * @param callback
     */
    @POST("/visit")
    void addSignIn(@Body HashMap<String, Object> map, Callback<LegWork> callback);

    @GET("/visit/bycustId")
    void getLegworks(@QueryMap HashMap<String, Object> map, Callback<PaginationX<LegWork>> callback);

    @GET("/visit/{id}")
    void getLegwork(@Path("id") String id, Callback<LegWork> callback);

    @GET("/customer/industry")
    void getIndustry(Callback<ArrayList<Industry>> callback);

    /**
     * 新建客户，查重
     */
    @GET("/customer/search")
    void getSerachRepeat(@QueryMap Map<String, Object> params, Callback<PaginationX<CustomerRepeatList>> callback);

    /**
     * 获取客户新建联系人的动态字段
     *
     * @param callback
     */
    @GET("/contact/fields")
    void getContactsField(Callback<ArrayList<ContactLeftExtras>> callback);

    /**
     * 获取客户联系人的详细数据
     *
     * @param callback
     */
    @GET("/contact/{customerId}")
    void getCustomerContacts(@Path("customerId") String id, Callback<Customer> callback);

    @POST("/customer/")
    void addNewCustomer(@Body Map<String, Object> map, Callback<Customer> callback);

    /**
     * 根据 key 获取后台设置信息
     * ?key=need_pictures_switcher
     *
     * @param map
     * @param callback
     */
    @GET("/config")
    void getSetInfo(@QueryMap Map<String, Object> map, Callback<SigninPictures> callback);

    /**
     * 新建客户,后台权限
     */
    @GET("/properties")
    void getAddCustomerJur(@QueryMap Map<String, Object> map, Callback<ArrayList<ContactLeftExtras>> callback);

    /**
     * 获取 客户 的订单
     *
     * @param callback
     */
    @GET("/order/cus")
    void getCutomerOrder(@QueryMap HashMap<String, Object> map, Callback<PaginationX<OrderListItem>> callback);

}
