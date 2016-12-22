package com.loyo.oa.v2.activityui.followup.api;

import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 跟进的网络接口
 * Created by jie on 16/12/22.
 */

public interface IFollowUp {
    /**
     * 评论
     */
    @POST("/comment")
    Observable<BaseBeanT<CommentModel>> requestComment(@Body Map<String, Object> params);

    /**
     * 删除评论
     */
    @DELETE("/comment/{id}")
    Observable<Object> deleteComment(@Path("id") String id);

    @GET("/customer/nearme")
    Observable<BaseBeanT<ArrayList<SigninSelectCustomer>>> getSiginiNearCustomer(@QueryMap Map<String, Object> params);

    /**
     * 我的团队,跟进数据
     */
    @GET("/saleactivity/mobile/sale/")
    Observable<BaseBeanT<PaginationX<FollowUpListModel>>> followUp(@QueryMap Map<String, Object> params);

    /**
     * 客户下的跟进数据
     */
    @GET("/saleactivity/{id}/")
    Observable<PaginationX<FollowUpGroupModel>> getCustomerFollowUpList(@Path("id") String id, @QueryMap Map<String, Object> params);


    /**
     * 跟进详情数据
     */
    @GET("/saleactivity/detail/")
    Observable<BaseBeanT<FollowUpListModel>> getFollowUpDetails(@QueryMap HashMap<String, Object> map);


    /**
     * 获取跟进列表 赛选的数据
     */
    @GET("/saleactivity/mobile/filters")
    Observable<FollowFilter> getFollowUpFilters();
}
