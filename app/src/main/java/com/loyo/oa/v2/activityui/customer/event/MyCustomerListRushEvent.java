package com.loyo.oa.v2.activityui.customer.event;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * EventBus的一个事件，用这个来处理客户的添加，修改和删除，通知详细页，列表页，及时更新数据
 * 数据的更细，统一使用EventBus，不再采用startAcrivityForResult
 */

public class MyCustomerListRushEvent extends CommonEvent<Customer>{
    //定义可能通知的类型，方便相应的地方，使用eventCode传递
    public static final int EVENT_CODE_ADD=0x1;
    public static final int EVENT_CODE_DEL=0x2;
    public static final int EVENT_CODE_UPDATE=0x3;
    public MyCustomerListRushEvent(Customer customer) {
        data=customer;
    }
}
