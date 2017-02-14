package com.loyo.oa.v2.customermanagement.event;

import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * Created by jie on 17/2/14.
 */

public class MyContactPushEvent extends CommonEvent<Contact> {

    public static final int EVENT_CODE_ADD    = 0x1;
    public static final int EVENT_CODE_UPDATE = 0x2;
}
