package com.loyo.oa.v2.activityui.signin.event;

import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.common.event.BaseEvent;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * 签到的时候，选择了客户，通知之前的页面
 * Created by jie on 17/2/7.
 */

public class SigninCustomerRushEvent extends CommonEvent<SigninSelectCustomer> {

    public SigninCustomerRushEvent(SigninSelectCustomer signinSelectCustomer) {
        data=signinSelectCustomer;
    }
}
