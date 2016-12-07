package com.loyo.oa.v2.activityui.clue.viewcontrol;

import android.view.ViewGroup;

import com.loyo.oa.v2.activityui.clue.model.ClueFollowGroupModel;
import com.loyo.oa.v2.activityui.clue.model.ClueFollowUpListModel;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/11/14.
 */

public interface ClueFollowUpListView {

    /*评论操作*/
    void commentEmbl(String id);

    /*删除附件操作*/
    void deleteCommentEmbl(String id);

    /*刷新列表数据*/
    void rushListData(boolean shw);

    /*发送评论成功操作*/
    void commentSuccessEmbl();

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(PaginationX<ClueFollowGroupModel> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl();

    ViewGroup getBottomMenuLayout();

}
