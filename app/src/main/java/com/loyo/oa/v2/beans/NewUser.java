package com.loyo.oa.v2.beans;

import android.util.Log;

import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.List;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版User
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class NewUser implements Serializable {
    private String id;  // "5600ff1f54418940bf233261",
    private String name;// "测试负责人",
    private String avatar;
//    private String deptId;
//
//    public String getDeptId() {
//        return deptId;
//    }
//
//    public NewUser setDeptId(String deptId) {
//        this.deptId = deptId;
//        return this;
//    }

    public static String GetNewUserNames(List<NewUser> users) {
        if (users == null || users.isEmpty()){
            return "";
        }

        StringBuffer sb = null;
        for (NewUser user : users) {
            if (sb == null) {
                sb = new StringBuffer();
                sb.append(user.getRealname());
            } else {
                sb.append(",").append(user.getRealname());
            }
        }
        return sb.toString();
    }

    public String getAvatar() {
        return avatar;
    }

    public NewUser setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getId() {
        return id;
    }

    public NewUser setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public NewUser setName(String name) {
        this.name = name;
        return this;
    }

    public String getRealname() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof User) {
                return id.equals(((User) o).getId());
        }

        if (!(o instanceof NewUser)) {
            return false;
        }

        NewUser user = (NewUser) o;

        return id.equals(user.id);
    }

    public boolean isCurrentUser() {
        return equals(MainApp.user);
    }

    public User toUser() {
        User user = new User();
        user.setAvatar(this.getAvatar());
        user.setId(this.getId());
        user.setRealname(this.getName());
        user.setName(this.getName());

        return user;
    }

}
