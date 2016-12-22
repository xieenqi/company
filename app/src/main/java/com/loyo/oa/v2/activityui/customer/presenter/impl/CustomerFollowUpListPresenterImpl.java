package com.loyo.oa.v2.activityui.customer.presenter.impl;

import android.content.Context;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.customer.presenter.CustomerFollowUpListPresenter;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerFollowUpListView;
import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public class CustomerFollowUpListPresenterImpl implements CustomerFollowUpListPresenter {

    private CustomerFollowUpListView crolView;
    private Context mContext;

    public CustomerFollowUpListPresenterImpl(CustomerFollowUpListView crolView, Context mContext) {
        this.crolView = crolView;
        this.mContext = mContext;
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
            public void onNext(BaseBeanT<CommentModel> commentModelBaseBeanT) {
                crolView.commentSuccessEmbl(commentModelBaseBeanT.data);
            }
        });
    }

    /**
     * 获取列表数据
     */
    @Override
    public void getListData(HashMap<String, Object> map, String id, final int page) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).getCustomerFollowUpList(id, map, new RCallback<PaginationX<FollowUpGroupModel>>() {
//            @Override
//            public void success(PaginationX<FollowUpGroupModel> paginationX, Response response) {
//                HttpErrorCheck.checkResponse("客户下的跟进", response);
//                if (paginationX != null && paginationX.getRecords() != null)
//                    crolView.getListDataSuccesseEmbl(paginationX);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error, crolView.getLoadingLayout());
//                crolView.getListDataErrorEmbl();
//                super.failure(error);
//            }
//        });

        FollowUpService.getCustomerFollowUpList(id,map).subscribe(new DefaultLoyoSubscriber<PaginationX<FollowUpGroupModel>>(crolView.getLoadingLayout()) {
            @Override
            public void onNext(PaginationX<FollowUpGroupModel> paginationX) {
                if (paginationX != null && paginationX.getRecords() != null)
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
