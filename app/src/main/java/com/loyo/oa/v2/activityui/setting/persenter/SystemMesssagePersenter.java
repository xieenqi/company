package com.loyo.oa.v2.activityui.setting.persenter;

import com.loyo.oa.v2.common.base.BasePersenter;

/**
 * 【我的讨论】页面persenter
 * Created by xeq on 16/10/13.
 */

public interface SystemMesssagePersenter extends BasePersenter {
    /**
     * 下拉刷新
     */
    void pullDown();

    /**
     * 上拉加载
     */
    void pullUp();
}
