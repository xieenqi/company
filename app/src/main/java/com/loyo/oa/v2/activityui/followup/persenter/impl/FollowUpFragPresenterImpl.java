package com.loyo.oa.v2.activityui.followup.persenter.impl;

import android.content.Context;

import com.loyo.oa.v2.activityui.followup.api.FollowUpService;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.followup.persenter.FollowUpFragPresenter;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowUpListView;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;

import java.util.HashMap;

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
        FollowUpService.deleteComment(id).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                crolView.rushListData(true);
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
     * 获取列表数据
     */
    @Override
    public void getListData(HashMap<String, Object> map, final int page) {
        FollowUpService.followUp(map)
                .subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<FollowUpListModel>>>(crolView.getLoadingLayout()) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                crolView.getListDataErrorEmbl();
            }

            @Override
            public void onNext(BaseBeanT<PaginationX<FollowUpListModel>> paginationX) {
                crolView.getListDataSuccesseEmbl(paginationX);
            }
        });
    }
}
