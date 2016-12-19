package com.loyo.oa.v2.activityui.clue.presenter.impl;

import android.content.Context;

import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.presenter.ClueFollowUpListPresenter;
import com.loyo.oa.v2.activityui.clue.viewcontrol.ClueFollowUpListView;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.point.ISigninOrFollowUp;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public class ClueFollowUpListPresenterImpl implements ClueFollowUpListPresenter {

    private ClueFollowUpListView crolView;
    private Context mContext;

    public ClueFollowUpListPresenterImpl(ClueFollowUpListView crolView, Context mContext) {
        this.crolView = crolView;
        this.mContext = mContext;
    }

    /**
     * 删除评论
     */
    @Override
    public void deleteComment(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).deleteComment(id, new RCallback<Object>() {
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
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).requestComment(map, new RCallback<BaseBeanT<CommentModel>>() {
            @Override
            public void success(BaseBeanT<CommentModel> object, Response response) {
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
    public void getListData(HashMap<String, Object> map, final int page) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).followUp(map, new RCallback<PaginationX<ClueFollowGroupModel>>() {
            @Override
            public void success(PaginationX<ClueFollowGroupModel> paginationX, Response response) {
                HttpErrorCheck.checkResponse("线索下跟进列表", response);
                if (paginationX != null && paginationX.getRecords() != null)
                    crolView.getListDataSuccesseEmbl(paginationX);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error, crolView.getLoading(), page == 1 ? true : false);
                crolView.getListDataErrorEmbl();
                super.failure(error);
            }
        });
    }
}
