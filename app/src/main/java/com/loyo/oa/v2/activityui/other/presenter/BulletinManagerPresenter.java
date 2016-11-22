package com.loyo.oa.v2.activityui.other.presenter;

import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;

/**
 * Created by yyy on 16/10/10.
 */

public interface BulletinManagerPresenter {

    /*获取列表数据*/
    void requestListData(int pageIndex,int pageSize, boolean isTopAdd);

    /*绑定数据*/
    void bindListData(PullToRefreshRecyclerView2 mRecycleView);

    /*权限认证*/
    void isPermission();

}
