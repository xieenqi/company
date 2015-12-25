package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable
public class User implements Serializable {

    public String id;
    public String company_id;
    public String username;
    public int gender;
    public String mobile;
    public boolean avaactivatedtar;
    public String simplePinyin;
    public ArrayList<UserInfo> depts = new ArrayList<>();
    public String avatar;
    public String birthDay;
    public long createdAt;
    public String email;
    public String fullPinyin;
    public String realname;
    public String tel;
    public String title;
    public long updatedAt;
    public String departmentsName;
    public String superiorId;
    public boolean isBQQ;
    public String weixinId;
    public String weixinUnionId;
    public String name;
    public static String imageUrl;
    public Role role;
    public Position shortPosition;
    public Department shortDept;

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
//    public User(String _id, String _RealName) {
//        id = _id;
//        realname = _RealName;
//    }
}
