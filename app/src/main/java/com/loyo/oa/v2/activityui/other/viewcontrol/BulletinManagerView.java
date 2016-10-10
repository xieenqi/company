package com.loyo.oa.v2.activityui.other.viewcontrol;

/**
 * Created by yyy on 16/10/10.
 */

public interface BulletinManagerView {

    /*新建权限通过*/
    void permissionSuccess();

    /*权限错误*/
    void permissionError();

    /*打开Loading*/
    void showLoading();

    /*绑定数据*/
    void bindListData();

    /*停止刷新*/
    void refreshCmpl();
}
