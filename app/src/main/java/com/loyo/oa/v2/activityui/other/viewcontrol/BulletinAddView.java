package com.loyo.oa.v2.activityui.other.viewcontrol;

import com.loyo.oa.v2.beans.Bulletin;

/**
 * Created by yyy on 16/10/9.
 */

public interface BulletinAddView {

    /*验证失败*/
    void verifyError(int code);

    /*验证通过*/
    void verifyPass();

    /*请求成功*/
    void onSuccess(Bulletin bulletin);

    /*请求失败*/
    void onError();

    /*关闭SweetAlert*/
    void dissweetAlert();

    /*打开Loading*/
    void showLoading();
}
