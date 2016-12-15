package com.loyo.oa.v2.point;


import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * 【拜访和跟进公用】
 * Created by yyy on 16/11/12.
 */

public interface ISigninOrFollowUp {


    /**
     * 客户下 拜访数据
     */
    @GET("/visit/m/bycustId/")
    void getCustomerSignin(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<SigninNewGroupModel>>> cb);

    /**
     * 我的拜访数据
     */
    @GET("/visit/self/")
    void selfSignin(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<SigninNewListModel>>> cb);

    /**
     * 团队拜访数据
     */
    @GET("/visit/team/")
    void teamSignin(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<SigninNewListModel>>> cb);

    /**
     * 评论
     */
    @POST("/comment")
    void requestComment(@Body Map<String, Object> params, Callback<Object> callback);

    /**
     * 删除评论
     */
    @DELETE("/comment/{id}")
    void deleteComment(@Path("id") String id, Callback<Object> callback);

    @GET("/customer/nearme")
    void getSiginiNearCustomer(@QueryMap Map<String, Object> params, Callback<BaseBeanT<ArrayList<SigninSelectCustomer>>> cb);

    /**
     * 我的团队,跟进数据
     */
    @GET("/saleactivity/mobile/sale/")
    void followUp(@QueryMap Map<String, Object> params, Callback<BaseBeanT<PaginationX<FollowUpListModel>>> cb);

    /**
     * 客户下的跟进数据
     */
    @GET("/saleactivity/{id}/")
    void getCustomerFollowUpList(@Path("id") String id,@QueryMap Map<String, Object> params, Callback<PaginationX<FollowUpGroupModel>> cb);

    /**
     * 拜访详情数据
     */
    @GET("/visit/detail/{id}/")
    void getSigninDetails(@Path("id") String id, @QueryMap HashMap<String, Object> map, Callback<BaseBeanT<SigninNewListModel>> cb);

    /**
     * 跟进详情数据
     */
    @GET("/saleactivity/detail/")
    void getFollowUpDetails(@QueryMap HashMap<String, Object> map, Callback<BaseBeanT<FollowUpListModel>> cb);

    /**
     * 拜访搜索客户
     */
    @GET("/customer/mobile/search/simplify")
    void signinSearchCutomer(@QueryMap HashMap<String, Object> map, Callback<BaseBeanT<PaginationX<SigninSelectCustomer>>> cb);


    /**
     * 获取跟进列表 赛选的数据
     */
    @GET("/saleactivity/mobile/filters")
    void getFollupFilters(Callback<ArrayList<FollowFilter>> cb);
}

