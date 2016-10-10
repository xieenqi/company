package com.loyo.oa.v2.activityui.other.presenter.Impl;

import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshRecycleView;

/**
 * Created by yyy on 16/10/10.
 */

public interface BulletinManagerPresenterImpl {

    /*获取列表数据*/
    void requestListData(int pageIndex,int pageSize);

    /*绑定数据*/
    void bindListData(PullToRefreshRecycleView mRecycleView);

    /*权限认证*/
    void isPermission();

}
