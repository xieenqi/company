package com.loyo.oa.v2.activityui.customer.presenter.impl;

import com.loyo.oa.v2.activityui.customer.presenter.SigninListFragPresenter;
import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.activityui.signinnew.presenter.SelfSigninListFragPresenter;
import com.loyo.oa.v2.activityui.signinnew.viewcontrol.SigninNewListView;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
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

    private SigninNewListView crolView;

    public SigninListFragPresenterImpl(SigninNewListView crolView){
        this.crolView = crolView;
    }

    /**
     * 删除评论
     * */
    @Override
    public void deleteComment(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).deleteComment(id, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                crolView.rushListData(false);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 发送评论
     * */
    @Override
    public void requestComment(HashMap<String,Object> map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).requestComment(map, new RCallback<Object>() {
            @Override
            public void success(Object object, Response response) {
                HttpErrorCheck.checkResponse("评论", response);
                crolView.commentSuccessEmbl();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    /**
     * 获取拜访列表数据
     * */
    @Override
    public void getListData(HashMap<String, Object> map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).getCustomerSignin(map, new RCallback<BaseBeanT<PaginationX<SigninNewListModel>>>() {
            @Override
            public void success(BaseBeanT<PaginationX<SigninNewListModel>> paginationX, Response response) {
                HttpErrorCheck.checkResponse("客户下拜访", response);
                crolView.getListDataSuccesseEmbl(paginationX);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                crolView.getListDataErrorEmbl();
                super.failure(error);
            }
        });
    }

}
