package com.loyo.oa.v2.db.bean;

/**
 * Created by EthanGong on 16/8/2.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.loyo.oa.v2.activityui.other.model.SelectUserData;
import com.loyo.oa.v2.beans.OrganizationalMember;

import java.io.Serializable;
import java.util.HashSet;

@DatabaseTable(tableName = "users")
public class DBUser implements Serializable {

    @DatabaseField(id = true)
    public String id;

    @DatabaseField
    public String name;

    @DatabaseField
    public int gender;

    @DatabaseField
    public String mobile;

    @DatabaseField
    public String avatar;

    @DatabaseField
    public boolean activated;

    @DatabaseField
    public String simplePinyin;

    @DatabaseField
    public String fullPinyin;

    @DatabaseField
    public String weixinId;

    @DatabaseField
    public String birthDay;

    @DatabaseField
    public String shortDeptNames;

    @DatabaseField
    public boolean bqqDeletable;

    @DatabaseField
    public boolean isSuperUser;

    @DatabaseField
    public Long deletedAt;

    @DatabaseField
    public boolean isBQQ;

    @DatabaseField(defaultValue = "1")
    public int depth;

    public boolean isCurrentUser;

    public HashSet<DBDepartment> depts = new HashSet<>();

    public SelectUserData selectUserData;

    public String getSortLetter() {

        String pinyin = pinyin();
        if (pinyin != null && pinyin.length() > 0) {
            String sortString = pinyin.substring(0, 1).toUpperCase();
            return sortString;
        }
        else {
            return "#";
        }
    }

    public String pinyin(){
        String pinyin = this.fullPinyin != null && this.fullPinyin.length()>0 ? this.fullPinyin:this.simplePinyin;
        if (pinyin != null && pinyin.length() > 0) {
            return pinyin;
        }
        else {
            return "#";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != DBUser.class) {
            return false;
        }

        DBUser d =( DBUser)obj;
        return id.equals(d.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * TODO:
     *
     * @return OrganizationalMember
     */
    public OrganizationalMember toShortUser() {
        OrganizationalMember user = new OrganizationalMember();
        user.setId(this.id);
        user.setName(this.name);
        user.setAvatar(this.avatar);
        return user;
    }
}

