package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    public String id;
    public String company_id;
    public String mobile;
    public String simplePinyin;
    public String avatar;
    public String birthDay;
    public String fullPinyin;
    public String realname;
    public String tel;
    public String title;
    public String departmentsName;
    public String superiorId;
    public String weixinId;
    public String name;

    public static String imageUrl;
    public Role role;
    public Position shortPosition;
    public Department shortDept;
    public ArrayList<UserInfo> depts = new ArrayList<>();

    public boolean isBQQ;
    public boolean index;
    public int gender;
    public long updatedAt;
    public long createdAt;

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static String getImageUrl() {
        return imageUrl;
    }

    public static void setImageUrl(String imageUrl) {
        User.imageUrl = imageUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof NewUser) {
            return id.equals(((NewUser) o).getId());
        }

        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id);
    }

    public boolean isCurrentUser() {
        return equals(MainApp.user);
    }

    public NewUser toShortUser() {
        NewUser user = new NewUser();
        user.setId(this.id);
        user.setName(this.getRealname());
        user.setAvatar(this.avatar);
        return user;
    }

    /**
     * 获取首字母当作GroupName
     *
     * @return
     */
    public String getGroupName() {
        if (!TextUtils.isEmpty(fullPinyin)) {
            return fullPinyin.substring(0, 1).toUpperCase();
        } else if (!TextUtils.isEmpty(fullPinyin)) {
            return fullPinyin.substring(0, 1).toUpperCase();
        }
        return "";
    }

    public String getRealname() {
        String rname = TextUtils.isEmpty(realname) ? name : realname;
        return TextUtils.isEmpty(rname) ? "" : rname;
    }
}
