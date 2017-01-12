package com.loyo.oa.v2.activityui.followup.persenter;

import android.widget.ListView;

import java.util.HashMap;

/**
 * Created by loyo_dev1 on 16/11/16.
 */

public interface FollowUpFragPresenter {

    /*删除评论*/
    void deleteComment(ListView list, int position, String id);

    /*发送评论*/
    void requestComment(HashMap<String,Object> map);

    /*获取列表数据*/
    void getListData(HashMap<String,Object> map);

}
