package com.loyo.oa.v2.activityui.setting.viewcontrol;

import com.loyo.oa.v2.common.BaseView;

/**
 * Created by xeq on 16/10/14.
 */

public interface SettingVControl extends BaseView {
    /**
     * 设定绑定电话相关
     *
     * @param cell
     */
    void setCell(String cell);

    /**
     * 设定缓存信息相关
     */
    void setCache(String cache);
}
