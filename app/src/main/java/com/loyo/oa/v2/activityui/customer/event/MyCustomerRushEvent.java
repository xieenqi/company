package com.loyo.oa.v2.activityui.customer.event;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * EventBus的一个事件，用这个来处理客户的添加，修改和删除，通知详细页，列表页，及时更新数据
 * 数据的更细，统一使用EventBus，不再采用startAcrivityForResult
 */

public class MyCustomerRushEvent extends CommonEvent<Customer>{
    //定义可能通知的类型，方便相应的地方，使用eventCode传递
    public static final int EVENT_CODE_ADD    = 0x1;
    public static final int EVENT_CODE_DEL    = 0x2;
    public static final int EVENT_CODE_UPDATE = 0x3;

    public static final int EVENT_SUB_CODE_INFO     = 0x1;//跟新客户信息
    public static final int EVENT_SUB_CODE_LABEL    = 0x2;//更新标签
    public static final int EVENT_SUB_CODE_RECYCLER = 0x3;//更新跟进时间，丢公海时间
    public static final int EVENT_SUB_CODE_STATE    = 0x4;//更新状态
    public static final int EVENT_SUB_CODE_LTC      = 0x5;//更新状态、标签、联系人

    public MyCustomerRushEvent() {
    }

    public MyCustomerRushEvent(Customer customer) {
        data = customer;
    }
}
