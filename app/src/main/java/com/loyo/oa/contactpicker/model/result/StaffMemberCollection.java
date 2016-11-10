package com.loyo.oa.contactpicker.model.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/18.
 */

public class StaffMemberCollection implements Serializable {
    public List<StaffMember> depts = new ArrayList<>();
    public List<StaffMember> users = new ArrayList<>();

    public List<String> departmentIds() {
        List<String> result = new ArrayList<>();
        for (StaffMember member:depts) {
            result.add(member.id);
        }
        return result;
    }

    public List<String> userIds() {
        List<String> result = new ArrayList<>();
        for (StaffMember member:users) {
            result.add(member.id);
        }
        return result;
    }
}
