package com.loyo.oa.v2.activityui.signinnew.viewcontrol;

import com.loyo.oa.v2.activityui.signinnew.model.SigninNewListModel;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/11/14.
 */

public interface SigninNewListView {

    /*评论操作*/
    void commentEmbl(int position);

    /*删除附件操作*/
    void deleteCommentEmbl(String id);

    /*刷新列表数据*/
    void rushListData(boolean shw);

    /*发送评论成功操作*/
    void commentSuccessEmbl();

    /*获取列表数据成功*/
    void getListDataSuccesseEmbl(BaseBeanT<PaginationX<SigninNewListModel>> paginationX);

    /*获取列表数据失败*/
    void getListDataErrorEmbl();

}
