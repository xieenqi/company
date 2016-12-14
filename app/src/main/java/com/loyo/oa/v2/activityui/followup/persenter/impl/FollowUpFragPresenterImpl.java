package com.loyo.oa.v2.activityui.followup.persenter.impl;

import android.content.Context;

import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.persenter.FollowUpFragPresenter;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public class FollowUpFragPresenterImpl implements FollowUpFragPresenter {

    private FollowUpListView crolView;
    private Context mContext;

    public FollowUpFragPresenterImpl(FollowUpListView crolView, Context mContext) {
        this.crolView = crolView;
        this.mContext = mContext;
    }

    /**
     * 删除评论
     */
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
     */
    @Override
    public void requestComment(HashMap<String, Object> map) {
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
     * 获取列表数据
     */
    @Override
    public void getListData(HashMap<String, Object> map) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).followUp(map, new RCallback<BaseBeanT<PaginationX<FollowUpListModel>>>() {
            @Override
            public void success(BaseBeanT<PaginationX<FollowUpListModel>> paginationX, Response response) {
                HttpErrorCheck.checkResponse("跟进列表", response);
                crolView.getListDataSuccesseEmbl(paginationX);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error, crolView.getLoadingLayout());
                crolView.getListDataErrorEmbl();
                super.failure(error);
            }
        });
    }
}
