package com.loyo.oa.v2.activityui.signin.viewcontrol;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.activityui.signin.bean.SigninNewListModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/11/14.
 */

public interface SigninListView {

    /*评论操作*/
    void commentEmbl(int position);

    /*删除附件操作*/
    void deleteCommentEmbl(String id);

    /*刷新列表数据*/
    void rushListData(boolean shw);

    /*发送评论成功操作*/
    void commentSuccessEmbl(CommentModel modle);

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(BaseBeanT<PaginationX<SigninNewListModel>> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl();

    LoadingLayout getLoadingView();

}
