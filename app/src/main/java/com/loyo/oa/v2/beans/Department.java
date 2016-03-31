package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activity.commonview.SelectUserHelper;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 通讯录部门bean
 */

public class Department implements Serializable, SelectUserHelper.SelectUserBase {

    private String id;
    private String xpath;
    private String name;
    private String superiorId;
    private String fullPinyin;
    private String simplePinyin;
    private long updatedAt;
    private long createdAt;
    private boolean isIndex;
    private ArrayList<User> users = new ArrayList<>();
    public String title = "";//职务名称


    public boolean isIndex() {
        return isIndex;
    }

    public void setIsIndex(boolean isIndex) {
        this.isIndex = isIndex;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    @Override
    public int getUserCount() {
        return users.size();
    }

    @Override
    public boolean isDepart() {
        return true;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDepartId() {
        return getId();
    }

    @Override
    public boolean equalsId(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        return id.equals(this.id);
    }

    @Override
    public String getAvater() {
        return SelectUserHelper.SelectUserBase.NULL_AVATAR;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimplePinyin() {
        return simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

    public String getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(String superiorId) {
        this.superiorId = superiorId;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public boolean isMyDept() {
        return TextUtils.equals(getId(), MainApp.user.depts.get(0).getShortDept().getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Department)) {
            return false;
        }

        Department that = (Department) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 获取首字母当作GroupName
     *
     * @return
     */
    public String getGroupName() {

        if (!TextUtils.isEmpty(getFullPinyin())) {
            return getFullPinyin().substring(0, 1).toUpperCase();
        } else if (!TextUtils.isEmpty(getSimplePinyin())) {
            return getSimplePinyin().substring(0, 1).toUpperCase();
        } else if (TextUtils.isEmpty(getFullPinyin()) || TextUtils.isEmpty(getSimplePinyin())) {
            //LogUtil.d(" #ddd  "+getName());
            return "# ";
        }

        return "";
    }
}
