package com.loyo.oa.v2.activityui.signin.persenter;

import com.loyo.oa.v2.common.base.BasePersenter;

/**
 * Created by xeq on 16/11/15.
 */

public interface SigninSelectCustomerPersenter extends BasePersenter {

    void oneGetNearCustomer(double longitude, double latitude);

    void pullDown();

    void pullUp();
}
