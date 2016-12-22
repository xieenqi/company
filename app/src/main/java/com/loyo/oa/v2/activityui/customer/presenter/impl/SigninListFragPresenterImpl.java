package com.loyo.oa.v2.activityui.customer.presenter.impl;

import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.customer.presenter.SigninListFragPresenter;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerSigninNewListView;
import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.api.SignInService;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.point.ISigninOrFollowUp;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【客户下】拜访签到
 * Created by yyy on 16/11/16.
 */

public class SigninListFragPresenterImpl implements SigninListFragPresenter {

    private CustomerSigninNewListView crolView;

    public SigninListFragPresenterImpl(CustomerSigninNewListView crolView) {
        this.crolView = crolView;
    }

    /**
     * 删除评论
     */
    @Override
    public void deleteComment(String id) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).deleteComment(id, new RCallback<Object>() {
//            @Override
//            public void success(Object object, Response response) {
//                HttpErrorCheck.checkResponse("评论", response);
//                crolView.rushListData(false);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//                super.failure(error);
//            }
//        });

        FollowUpService.deleteComment(id).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                crolView.rushListData(false);
            }
        });
    }

    /**
     * 发送评论
     */
    @Override
    public void requestComment(HashMap<String, Object> map) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).requestComment(map, new RCallback<BaseBeanT<CommentModel>>() {
//            @Override
//            public void success(BaseBeanT<CommentModel> object, Response response) {
//                HttpErrorCheck.checkResponse("评论", response);
//                crolView.commentSuccessEmbl(object.data);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error);
//                super.failure(error);
//            }
//        });
        FollowUpService.requestComment(map).subscribe(new DefaultLoyoSubscriber<BaseBeanT<CommentModel>>() {
            @Override
            public void onNext(BaseBeanT<CommentModel> object) {
                crolView.commentSuccessEmbl(object.data);
            }
        });
    }

    /**
     * 获取拜访列表数据
     */
    @Override
    public void getListData(HashMap<String, Object> map, final int page) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).getCustomerSignin(map, new RCallback<BaseBeanT<PaginationX<SigninNewGroupModel>>>() {
//            @Override
//            public void success(BaseBeanT<PaginationX<SigninNewGroupModel>> paginationX, Response response) {
//                HttpErrorCheck.checkResponse("客户下拜访", response);
//                crolView.getListDataSuccesseEmbl(paginationX);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error, crolView.getLoading());
//                crolView.getListDataErrorEmbl();
//                super.failure(error);
//            }
//        });

        SignInService.getCustomerSignIn(map).subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<SigninNewGroupModel>>>(crolView.getLoading()) {
            @Override
            public void onNext(BaseBeanT<PaginationX<SigninNewGroupModel>> paginationX) {
                crolView.getListDataSuccesseEmbl(paginationX);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                crolView.getListDataErrorEmbl();
            }
        });
    }

}
