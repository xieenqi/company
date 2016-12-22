package com.loyo.oa.v2.activityui.followup.api;


import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * 跟进的服务
 * Created by jie on 16/12/22.
 */

public class FollowUpService {
    //评论
    public static Observable<BaseBeanT<CommentModel>> requestComment(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .requestComment(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<CommentModel>>compatApplySchedulers());
    }

    //删除评论
    public static Observable<Object> deleteComment(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .deleteComment(id)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }


    public static Observable<BaseBeanT<ArrayList<SigninSelectCustomer>>> getSiginiNearCustomer(Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .getSiginiNearCustomer(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<ArrayList<SigninSelectCustomer>>>compatApplySchedulers());
    }

    //我的团队,跟进数据
    public static Observable<BaseBeanT<PaginationX<FollowUpListModel>>> followUp(Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .followUp(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<PaginationX<FollowUpListModel>>>compatApplySchedulers());
    }

    // 客户下的跟进数据
    public static Observable<PaginationX<FollowUpGroupModel>> getCustomerFollowUpList(String id, Map<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .getCustomerFollowUpList(id,params)
                        .compose(RetrofitAdapterFactory.<PaginationX<FollowUpGroupModel>>compatApplySchedulers());
    }

    //跟进详情数据
    public static Observable<BaseBeanT<FollowUpListModel>> getFollowUpDetails(HashMap<String, Object> params) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .getFollowUpDetails(params)
                        .compose(RetrofitAdapterFactory.<BaseBeanT<FollowUpListModel>>compatApplySchedulers());
    }

    //获取跟进列表 赛选的数据
    public static Observable<FollowFilter> getFollowUpFilters() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IFollowUp.class)
                        .getFollowUpFilters()
                        .compose(RetrofitAdapterFactory.<FollowFilter>compatApplySchedulers());
    }


}
