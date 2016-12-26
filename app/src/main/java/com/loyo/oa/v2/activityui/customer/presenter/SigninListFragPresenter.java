package com.loyo.oa.v2.activityui.customer.presenter;

import java.util.HashMap;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public interface SigninListFragPresenter {

    /*删除评论*/
    void deleteComment(String id);

    /*发送评论*/
    void requestComment(HashMap<String, Object> map);

    /*获取列表数据*/
    void getListData(HashMap<String, Object> map,int page);

}
