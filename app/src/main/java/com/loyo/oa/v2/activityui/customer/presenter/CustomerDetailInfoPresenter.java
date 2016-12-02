package com.loyo.oa.v2.activityui.customer.presenter;

import android.app.Activity;

import com.loyo.oa.v2.activityui.customer.model.Customer;

/**
 * 【客户详情】Presenter
 * Created by yyy on 16/11/7.
 */

public interface CustomerDetailInfoPresenter {

    /*丢公海操作*/
    void toPublic(String id);

    /*删除操作*/
    void delete(String id);

    /*获取参与人权限*/
    void getMembersRoot();

    /*获取客户详情信息*/
    void getData(String id);

    /*判断是否为参与人*/
    boolean isMenber(Customer mCustomer);

    /*显示编辑客户弹出框*/
    void showEditPopu(Activity mActivity);

    /*拨打电话弹出框*/
    void paymentSet(Activity mActivity,String phone, int callType,String name);

    /*电话号码格式验证*/
    void isMobile(Activity mActivity, String phone, int callType, String name);

}
