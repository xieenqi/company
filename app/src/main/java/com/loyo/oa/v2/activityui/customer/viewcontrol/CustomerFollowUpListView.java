package com.loyo.oa.v2.activityui.customer.viewcontrol;

import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.customer.model.FollowUpGroupModel;
import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by loyo_dev1 on 16/11/23.
 */

public interface CustomerFollowUpListView {

    /*评论操作*/
    void commentEmbl(String id);

    /*删除附件操作*/
    void deleteCommentEmbl(String id);

    /*刷新列表数据*/
    void rushListData(boolean shw);

    /*发送评论成功操作*/
    void commentSuccessEmbl();

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(PaginationX<FollowUpGroupModel> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl();

    ViewGroup getBottomMenuLayout();

    LoadingLayout getLoadingLayout();

}
