package com.loyo.oa.v2.activityui.other.viewcontrol;

import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.common.BaseView;

/**
 * Created by yyy on 16/10/9.
 */

public interface BulletinAddView extends BaseView{

    /*验证失败*/
    void verifyError(int code);

    /*验证通过*/
    void verifySuccess(String title,String content);

    /*请求成功*/
    void onSuccess(Bulletin bulletin);

    /*请求失败*/
    void onError();

    /*关闭SweetAlert*/
    void dissweetAlert();

    /*打开Loading*/
    void showLoading();

    /*设置人员名字*/
    void setReceiver(String name);
}
