package com.loyo.oa.v2.activityui.customer.event;

import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * 生成商务电话事件
 */

public class RefreshCustomerEvent extends CommonEvent<String> {
    public RefreshCustomerEvent(String customerId) {
        this.data = customerId;
    }
}
