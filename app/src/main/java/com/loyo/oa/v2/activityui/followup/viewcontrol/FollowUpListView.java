package com.loyo.oa.v2.activityui.followup.viewcontrol;

import android.widget.ListView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.followup.adapter.ListOrDetailsCommentAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

import java.util.ArrayList;

/**
 * Created by yyy on 16/11/14.
 */

public interface FollowUpListView {

    /*评论操作*/
    void commentEmbl(int position);

    /*删除附件操作*/
    void deleteCommentEmbl(ListView adapter, int position, String id);

    /*在删除了评论的时候，刷新评论ui*/
    void rushListData(ListView list, int position);

    /*发送评论成功操作*/
    void commentSuccessEmbl(CommentModel modle);

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(BaseBeanT<PaginationX<FollowUpListModel>> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl(Throwable e);

    LoadingLayout getLoadingLayout();
}
