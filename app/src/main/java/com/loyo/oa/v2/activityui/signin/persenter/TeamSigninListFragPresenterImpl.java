package com.loyo.oa.v2.activityui.signin.persenter;

import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninListView;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
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

public class TeamSigninListFragPresenterImpl implements TeamSigninListFragPresenter {

    private SigninListView crolView;

    public TeamSigninListFragPresenterImpl(SigninListView crolView) {
        this.crolView = crolView;
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
                crolView.commentSuccessEmbl(object.data);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    @Override
    public void getListData(HashMap<String, Object> map, final int page) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).teamSignin(map, new RCallback<BaseBeanT<PaginationX<SigninNewListModel>>>() {
            @Override
            public void success(BaseBeanT<PaginationX<SigninNewListModel>> paginationX, Response response) {
                HttpErrorCheck.checkResponse("团队拜访", response);
                crolView.getListDataSuccesseEmbl(paginationX);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error, crolView.getLoadingView(), page == 1 ? true : false);
                crolView.getListDataErrorEmbl();
                super.failure(error);
            }
        });
    }

}
