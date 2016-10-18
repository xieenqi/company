package com.loyo.oa.contactpicker.model;

import com.loyo.oa.contactpicker.model.result.StaffMember;

/**
 * Created by EthanGong on 2016/10/17.
 */

public abstract class PickedModel {
    public boolean isDepartment;

    public abstract String getDisplayName();
    public abstract String getDisplayAvatar();
    public abstract StaffMember toStaffMember();
}
