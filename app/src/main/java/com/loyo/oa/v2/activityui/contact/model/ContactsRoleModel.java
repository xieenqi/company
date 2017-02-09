package com.loyo.oa.v2.activityui.contact.model;


import java.io.Serializable;

/**
 * 联系人 角色 模型
 * Created by jie on 17/2/8.
 */

public class ContactsRoleModel implements Serializable {
    public String id;
    public String name;
    public Boolean isSys;

    @Override
    public String toString() {
        return name;
    }
}
