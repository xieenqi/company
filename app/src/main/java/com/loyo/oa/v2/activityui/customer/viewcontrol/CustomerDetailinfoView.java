package com.loyo.oa.v2.activityui.customer.viewcontrol;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.BaseView;

/**
 * Created by yyy on 16/11/7.
 */

public interface CustomerDetailinfoView extends BaseView{

    /*投入公海成功处理*/
    void toPublicEmbl();

    /*删除操作成功处理*/
    void deleteEmbl();

    /*获取参与人权限*/
    void getMembersRootEmbl(MembersRoot membersRoot);

    /*获取详情数据成功处理*/
    void getDataSuccessEmbl(Customer customer);

    /*获取详情数据失败处理*/
    void getDataErrorEmle();

    /*弹出通用提示框操作*/
    void setPopViewEmbl(boolean mespray,String message);

    LoadingLayout getLoadigLayout();

    LoyoProgressHUD getHUD();
}
