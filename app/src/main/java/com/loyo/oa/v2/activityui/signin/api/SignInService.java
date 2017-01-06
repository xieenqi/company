package com.loyo.oa.v2.activityui.signin.api;

import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.followup.api.IFollowUp;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * 拜访的网络接口
 * Created by jie on 16/12/22.
 */

public class SignInService {
    //客户下 拜访数据
    public static Observable<BaseBeanT<PaginationX<SigninNewGroupModel>>> getCustomerSignIn(Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISignIn.class)
                        .getCustomerSignIn(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<PaginationX<SigninNewGroupModel>>>compatApplySchedulers());
    }

    //我的拜访数据
    public static Observable<BaseBeanT<PaginationX<SigninNewListModel>>> selfSignIn(Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISignIn.class)
                        .selfSignIn(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<PaginationX<SigninNewListModel>>>compatApplySchedulers());
    }

    //团队拜访数据
    public static Observable<BaseBeanT<PaginationX<SigninNewListModel>>> teamSignIn(Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISignIn.class)
                        .teamSignIn(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<PaginationX<SigninNewListModel>>>compatApplySchedulers());
    }


    //拜访详情数据
    public static Observable<BaseBeanT<SigninNewListModel>> getSignInDetails(String id,Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(ISignIn.class)
                        .getSignInDetails(id,params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<SigninNewListModel>>compatApplySchedulers());
    }

    //拜访搜索客户
    public static Observable<BaseBeanT<PaginationX<SigninSelectCustomer>>> signInSearchCutomer(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_STATISTICS())
                        .create(ISignIn.class)
                        .signInSearchCutomer(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<PaginationX<SigninSelectCustomer>>>compatApplySchedulers());
    }

}
