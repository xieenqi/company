package com.loyo.oa.v2.activityui.discuss.persenter;

import com.loyo.oa.v2.common.base.BasePersenter;

/**
 * 【我的讨论】页面persenter
 * Created by xeq on 16/10/13.
 */

public interface MyDiscussPersenter extends BasePersenter {

    /**
     * 跳转到@我的页面
     */
    void skipAtMy();

    /**
     * 点击item 查看讨论详情以及发送消息
     */
    void openItem();

    /**
     * 下拉刷新
     */
    void onPullDown();

    /**
     * 上拉加载更多
     */
    void onPullUp();
}
