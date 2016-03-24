package com.loyo.oa.v2.point;

import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.beans.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.CustomerRepeatList;
import com.loyo.oa.v2.beans.Demand;
import com.loyo.oa.v2.beans.Industry;
import com.loyo.oa.v2.beans.LegWork;
import com.loyo.oa.v2.beans.NearCount;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Product;
import com.loyo.oa.v2.beans.Province;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.beans.SaleStage;
import com.loyo.oa.v2.beans.Tag;

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

    @GET("/customer/selfsearch")
    void searchMyCustomers(@QueryMap Map<String, Object> params, retrofit.Callback<PaginationX<Customer>> cb);

    @GET("/customer/teamsearch")
    void searchTeamCustomers(@QueryMap Map<String, Object> params, retrofit.Callback<PaginationX<Customer>> cb);

    @GET("/customer/sharedsearch")
    void searchSharedCustomers(@QueryMap Map<String, Object> params, retrofit.Callback<PaginationX<Customer>> cb);

    /**
     * 获取个人附近客户
     *
     * @param lat
     * @param lon
     * @param cb
     */
    @GET("/customer/selfnear")
    void getNearCustomers(@Query("lat") String lat, @Query("lon") String lon, retrofit.Callback<ArrayList<Customer>> cb);

    /**
     * name "客户名称"	string	form<BR/>
     * loc		"客户位置信息"	models.customer.Location	form<BR/>
     * industry		"行业"	models.Industry	form
     * regional		"地区"	models.customer.Regional	form<BR/>
     * tags		false "标签"	[]<BR/>
     * ModelModel Schema<BR/>
     * CustomerTag <BR/>
     * summary		"简介"	string	form<BR/>
     * owner		"项目负责人"	tmodels.Member	form<BR/>
     * members		false "项目成员"	[]	Member<BR/>
     * extDatas		false "自定义字段值"	[]	DynamicData<BR/>
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
     *
     * @param id
     * @param callback
     */
    @GET("/customer/{id}")
    void getCustomerById(@Path("id") String id, Callback<Customer> callback);



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


    @GET("/demand/{id}/list")
    void getDemands(@Path("id") String id, @QueryMap HashMap<String, Object> map, Callback<PaginationX<Demand>> cb);

    @GET("/demand/{id}")
    void getDemand(@Path("id") String id, Callback<Demand> cb);

    @GET("/losereason/")
    void getLoseReasons(Callback<ArrayList<CommonTag>> cb);

    /**
     * 新增购买意向<BR/>
     * customerId		"客户id"	string	form<BR/>
     * productId		"产品id"	string	form<BR/>
     * saleStage		"销售阶段"	models.setting.SaleStage	form<BR/>
     * loseIds		false "丢单原因id数组"	[]	string<BR/>
     * actualNum		"实际数量"	float32	form<BR/>
     * actualPrice		"实际价格"	float32	form<BR/>
     * estimatedNum		"预估数量"	float32	form<BR/>
     * estimatedPrice		"预估价格"	float32	form<BR/>
     * memo 备注
     *
     * @param map
     * @param callback
     */
    @POST("/demand/")
    void addDemand(@Body HashMap<String, Object> map, Callback<Demand> callback);

    /**
     * 更新购买意向<BR/>
     * productId		"产品id"	string	form<BR/>
     * saleStage		"销售阶段"	models.setting.SaleStage	form<BR/>
     * loseIds		false "输单原因id"	[]	string<BR/>
     * wfId		"赢单审批流程id"	string	form<BR/>
     * wfState		"赢单审批流程状态"	int	form<BR/>
     * actualNum		"实际数量"	float32	form<BR/>
     * actualPrice		"实际价格"	float32	form<BR/>
     * estimatedNum		"预估数量"	float32	form<BR/>
     * estimatedPrice		"预估价格"	float32	form<BR/>
     * memo		"备注"	string	form
     *
     * @param map
     * @param callback
     */
    @PUT("/demand/{id}")
    void updateDemand(@Path("id") String id, @Body HashMap<String, Object> map, Callback<Demand> callback);


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

    @GET("/customer/regional")
    void getDistricts(Callback<ArrayList<Province>> callback);

    /**
     * 新建客户，查重
     */
    @GET("/customer/search")
    void getSerachRepeat(@QueryMap Map<String, Object> params, Callback<PaginationX<CustomerRepeatList>> callback);

    /**
     * 获取客户新建联系人的动态字段
     * @param callback
     */
    @GET("/contact/fields")
    void getContactsField(Callback<ArrayList<Province>> callback);
}
