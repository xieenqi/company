package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 15/12/18.
 */
public class Members implements Serializable {

    public ArrayList<NewUser> users = new ArrayList<>();

    public ArrayList<NewUser> depts = new ArrayList<>();


    /*获取所有参与人*/
    public ArrayList<NewUser> getAllData() {
        ArrayList<NewUser> newData = new ArrayList<NewUser>();
        if (users != null) {
            newData.addAll(users);
        }
        if (depts != null) {
            newData.addAll(depts);
        }
        return newData;
    }

    public ArrayList<NewUser> getAllDepts() {
        ArrayList<NewUser> newDate = new ArrayList<>();
        if (depts != null) {
            newDate.addAll(depts);
        }
        return newDate;
    }

}
