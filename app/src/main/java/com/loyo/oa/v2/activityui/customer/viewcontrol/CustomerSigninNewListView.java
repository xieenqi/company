package com.loyo.oa.v2.activityui.customer.viewcontrol;

/**
 * Created by loyo_dev1 on 16/12/5.
 */

import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.customer.model.SigninNewGroupModel;
import com.loyo.oa.v2.activityui.signin.bean.CommentModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/11/14.
 */

public interface CustomerSigninNewListView {

    /*评论操作*/
    void commentEmbl(String id, int parent, int child);

    /*删除附件操作*/
    void deleteCommentEmbl(String id);

    /*刷新列表数据*/
    void rushListData(boolean shw);

    /*发送评论成功操作*/
    void commentSuccessEmbl(CommentModel model);

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(BaseBeanT<PaginationX<SigninNewGroupModel>> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl();

    ViewGroup getBottomMenuLayout();

    LoadingLayout getLoading();

}
