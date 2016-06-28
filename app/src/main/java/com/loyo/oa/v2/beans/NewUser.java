package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.ui.activity.other.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版User
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class NewUser implements Serializable {

    private String id;
    private String name;
    private String avatar;
    private String xpath;
    private List<NewUser> users = new ArrayList<>();


    public List<NewUser> getUsers() {
        return users;
    }

    public void setUsers(List<NewUser> users) {
        this.users = users;
    }

    public static String GetNewUserNames(List<NewUser> users) {
        if (users == null || users.isEmpty()) {
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

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
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
        return TextUtils.isEmpty(name) ? " " : name;
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
            return ((User) o).id.equals(id);
        }

        if (!(o instanceof NewUser)) {
            return false;
        }

        NewUser user = (NewUser) o;

        return (user.id).equals(id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isCurrentUser() {
        return equals(MainApp.user);
    }

    public User toUser() {
        User user = new User();
        user.avatar = this.getAvatar();
        user.id = this.getId();
        user.realname = this.getName();
        user.name = this.getName();

        return user;
    }

}
