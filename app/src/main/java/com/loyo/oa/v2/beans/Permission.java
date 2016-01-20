package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/1/20.
 */
public class Permission implements Serializable{

    public String id;

    public String roleId;

    public ArrayList<Suites> suites = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
