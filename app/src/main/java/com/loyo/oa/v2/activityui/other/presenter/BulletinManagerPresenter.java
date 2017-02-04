package com.loyo.oa.v2.activityui.other.presenter;

import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.PaginationX;

/**
 * Created by yyy on 16/10/10.
 */

public interface BulletinManagerPresenter {

    /*获取列表数据*/
    void requestListData(PaginationX<Bulletin> paginationX);

    /*绑定数据*/
    void bindListData(PullToRefreshRecyclerView2 mRecycleView,PaginationX<Bulletin> paginationX);

    /*权限认证*/
    void isPermission();

}
