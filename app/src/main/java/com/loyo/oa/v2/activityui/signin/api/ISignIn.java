package com.loyo.oa.v2.activityui.signin.api;

import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * 拜访的网络接口
 * Created by jie on 16/12/22.
 */

public interface ISignIn {
    /**
     * 客户下 拜访数据
     */
    @GET("/visit/m/bycustId/")
    Observable<BaseBeanT<PaginationX<SigninNewGroupModel>>> getCustomerSignIn(@QueryMap Map<String, Object> params);

    /**
     * 我的拜访数据
     */
    @GET("/visit/self/")
    Observable<BaseBeanT<PaginationX<SigninNewListModel>>> selfSignIn(@QueryMap Map<String, Object> params);

    /**
     * 团队拜访数据
     */
    @GET("/visit/team/")
    Observable<BaseBeanT<PaginationX<SigninNewListModel>>> teamSignIn(@QueryMap Map<String, Object> params);

    /**
     * 拜访详情数据
     */
    @GET("/visit/detail/{id}/")
    Observable<BaseBeanT<SigninNewListModel>> getSignInDetails(@Path("id") String id, @QueryMap Map<String, Object> map);

    /**
     * 拜访搜索客户
     */
    @GET("/customer/mobile/search/simplify")
    Observable<BaseBeanT<PaginationX<SigninSelectCustomer>>> signInSearchCutomer(@QueryMap HashMap<String, Object> map);


}
