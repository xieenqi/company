package com.loyo.oa.contactpicker.model.event;

import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.common.event.CommonEvent;

/**
 * Created by EthanGong on 2016/10/18.
 */

public class ContactPickedEvent extends CommonEvent<StaffMemberCollection> {
    public ContactPickedEvent(StaffMemberCollection collection){
        this.data = collection;
    }
}
