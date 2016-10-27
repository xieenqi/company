package com.loyo.oa.v2.activityui.customer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ContactsGroup {

    private String groupName;
    List<Department> departments = new ArrayList<>();

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
