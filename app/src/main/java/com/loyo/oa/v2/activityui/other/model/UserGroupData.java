package com.loyo.oa.v2.activityui.other.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class UserGroupData {
    private String groupName;
    private ArrayList<User> lstUser;

    private String departmentId;

    public String getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(String departmentId)
    {
        this.departmentId = departmentId;
    }

    public UserGroupData() {
        lstUser = new ArrayList<User>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<User> getLstUser() {
        return lstUser;
    }

    public void setLstUser(ArrayList<User> lstUser) {
        this.lstUser = lstUser;
    }
}
