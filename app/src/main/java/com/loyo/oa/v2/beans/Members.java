package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 15/12/18.
 */
public class Members implements Serializable {

    public ArrayList<OrganizationalMember> users = new ArrayList<>();

    public ArrayList<OrganizationalMember> depts = new ArrayList<>();


    /*获取所有参与人*/
    public ArrayList<OrganizationalMember> getAllData() {
        ArrayList<OrganizationalMember> newData = new ArrayList<OrganizationalMember>();
        if (users != null) {
            newData.addAll(users);
        }
        if (depts != null) {
            newData.addAll(depts);
        }
        return newData;
    }

    public ArrayList<OrganizationalMember> getAllDepts() {
        ArrayList<OrganizationalMember> newDate = new ArrayList<>();
        if (depts != null) {
            newDate.addAll(depts);
        }
        return newDate;
    }

}
