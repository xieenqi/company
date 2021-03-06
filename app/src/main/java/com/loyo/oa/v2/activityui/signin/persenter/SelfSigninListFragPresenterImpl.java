package com.loyo.oa.v2.activityui.signin.persenter;

import android.widget.ListView;

import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.api.SignInService;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninListView;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.HashMap;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public class SelfSigninListFragPresenterImpl implements SelfSigninListFragPresenter {

    private SigninListView crolView;

    public SelfSigninListFragPresenterImpl(SigninListView crolView) {
        this.crolView = crolView;
    }

    /**
     * 删除评论
     */
    @Override
    public void deleteComment(final ListView list, final int position, String id) {

        FollowUpService.deleteComment(id).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                crolView.rushListData(list,position);
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

    @Override
    public void getListData(HashMap<String, Object> map, final int page) {
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninOrFollowUp.class).selfSignin(map, new RCallback<BaseBeanT<PaginationX<SigninNewListModel>>>() {
//            @Override
//            public void success(BaseBeanT<PaginationX<SigninNewListModel>> paginationX, Response response) {
//                HttpErrorCheck.checkResponse("我的拜访", response);
//                crolView.getListDataSuccesseEmbl(paginationX);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                HttpErrorCheck.checkError(error, crolView.getLoadingView());
//                crolView.getListDataErrorEmbl();
//                super.failure(error);
//            }
//        });

        SignInService.selfSignIn(map)
                .subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<SigninNewListModel>>>(crolView.getLoadingView()) {
            @Override
            public void onError(Throwable e) {
                crolView.getListDataErrorEmbl(e);
            }

            @Override
            public void onNext(BaseBeanT<PaginationX<SigninNewListModel>> paginationX) {
                crolView.getListDataSuccesseEmbl(paginationX);
            }
        });
    }

}
