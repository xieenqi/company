package com.loyo.oa.v2.activityui.other.viewcontrol;

import com.loyo.oa.v2.common.BaseView;

/**
 * Created by yyy on 16/10/10.
 */

public interface BulletinManagerView extends BaseView{

    /*新建权限通过*/
    void permissionSuccess();

    /*绑定数据*/
    void bindListData();

    /*停止刷新*/
    void refreshCmpl();
}
