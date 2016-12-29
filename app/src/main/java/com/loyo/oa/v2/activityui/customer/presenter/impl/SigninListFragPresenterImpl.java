package com.loyo.oa.v2.activityui.customer.presenter.impl;

import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.customer.presenter.SigninListFragPresenter;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerSigninNewListView;
import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.api.SignInService;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.HashMap;

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
        SignInService.getCustomerSignIn(map)
                .subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<SigninNewGroupModel>>>(crolView.getLoading()) {
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
