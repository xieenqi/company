package com.loyo.oa.v2.activityui.signin.viewcontrol;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.common.BaseView;

import java.util.ArrayList;

/**
 * Created by xeq on 16/11/15.
 */

public interface SigninSelectCustomerVControl extends BaseView {
    void getDataComplete();

    void bindData(ArrayList<SigninSelectCustomer> data);

    LoadingLayout getLoadingLayout();
}
