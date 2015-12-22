package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 15/12/18.
 */
public class Members implements Serializable {

    public ArrayList<NewUser> users;

    public ArrayList<NewUser> depts;


//    public ArrayList<NewUser> getUsers() {
//        return users;
//    }
//
//    public void setUsers(ArrayList<NewUser> users) {
//        this.users = users;
//    }
//
//    public ArrayList<NewUser> getDepts() {
//        return depts;
//    }
//
//    public void setDepts(ArrayList<NewUser> depts) {
//        this.depts = depts;
//    }

    /*获取参与人*/
    public ArrayList<NewUser> getAllData() {
        ArrayList<NewUser> newData=new ArrayList<NewUser>();
        if(users!=null){
            newData.addAll(users);
        }
        if(depts!=null){
            newData.addAll(depts);
        }
        return newData;
    }
}
