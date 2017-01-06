package com.loyo.oa.v2.activityui.clue.presenter.impl;

import android.content.Context;

import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.presenter.ClueFollowUpListPresenter;
import com.loyo.oa.v2.activityui.clue.viewcontrol.ClueFollowUpListView;
import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.HashMap;

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
    public void getListData(HashMap<String, Object> map, final int page) {
        ClueService.followUp(map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<ClueFollowGroupModel>>(crolView.getLoading()) {
            @Override
            public void onNext(PaginationX<ClueFollowGroupModel> paginationX) {
                if (paginationX != null && paginationX.getRecords() != null) {
                    crolView.getListDataSuccesseEmbl(paginationX);
                }
            }

            @Override
            public void onError(Throwable e) {
                /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type = page != 1 ?
                        LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                LoyoErrorChecker.checkLoyoError(e, type, crolView.getLoading());
                crolView.getListDataErrorEmbl();
            }
        });
    }
}
