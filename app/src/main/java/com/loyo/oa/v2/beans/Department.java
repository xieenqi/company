package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.ui.activity.commonview.SelectUserHelper;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 通讯录部门bean
 */

public class Department implements Serializable, SelectUserHelper.SelectUserBase {
    public String id;
    public String xpath;
    public String name;
    public String superiorId;
    public String simplePinyin;
    public ArrayList<User> users = new ArrayList<>();
    public String userNum;

    public boolean isIndex;
    public String fullPinyin;
    public long updatedAt;
    public long createdAt;
    public String title = "";//职务名称

    // 部门被选中, 或部门中的用户被全选时的回调
//    private SelectUserHelper.SelectDepartmentCallback mSelectDepartmentCallback;
//    private SelectUserHelper.SelectUserCallback mUserCallback = new SelectUserHelper.SelectUserCallback() {
//        @Override
//        public void onSelectUser(User user, boolean notify) {
//            if (user.isIndex()) {
//                boolean change = false;
//                boolean toUserIndex = false;
//                for (int i = 0; i < users.size(); i++) {
//                    User u = users.get(i);
////                    if (!u.isIndex() && !u.equalsId(user.getId())) {
////                        return;
////                    }
//                    if (u.equalsId(user.getId()) && u.isIndex() != user.isIndex()  ) {
//                        u.setIndex_(user.isIndex());
//                        toUserIndex = true;
//                    } else {
//                        if (!u.isIndex()) {
//                            change = true;
//                        }
//                    }
//                    if (change && toUserIndex) {
//                        break;
//                    }
//                }
//                setIsIndex(true, true, notify);
//            } else {
//                for (int i = 0; i < getUsers().size(); i++) {
//                    User u = users.get(i);
//                    if (u.equalsId(user.getId())) {
//                        u.setIndex(user.isIndex());
//                        break;
//                    }
//                }
//                if (isIndex()) {
//                    setIsIndex(false, true, notify);
//                }
//            }
//        }
//    };

//    /**
//     * 为部门类中所有人添加选中回调
//     */
//    public void startUserSelectCallback() {
//        for (int i = 0; i < users.size(); i++) {
//            users.get(i).addSelectUserCallback(mUserCallback);
//        }
//    }
//
//    /**
//     * 为Department添加选中监听
//     *
//     * @param mSelectUserCallback
//     */
//    public void setSelectDepartmentCallback(SelectUserHelper.SelectDepartmentCallback mSelectUserCallback) {
//        this.mSelectDepartmentCallback = mSelectUserCallback;
//    }

    public boolean isIndex() {
        return isIndex;
    }

    public void setIsIndex(boolean isIndex) {
        this.isIndex = isIndex;
    }

//    /**
//     * 设置选中状态
//     *
//     * @param isIndex
//     * @param isUserSelect 判断是否是单个用户操作触发
//     * @param notify       是否回调
//     */
//    public void setIsIndex(boolean isIndex, boolean isUserSelect, boolean notify) {
//        if (isIndex == this.isIndex)
//            return;
//        if (!isUserSelect) {
//            for (int i = 0; i < users.size(); i++) {
//                users.get(i).setIndex(isIndex, false);
//            }
//        }
//        boolean oldIndex = this.isIndex;
//        this.isIndex = isIndex;
//        if (mSelectDepartmentCallback != null && notify) {
//            mSelectDepartmentCallback.onSelectDepartment(this, oldIndex, isUserSelect);
//        }
//    }
//
//    public void setIsIndex(boolean isIndex, boolean isUserSelect) {
//        setIsIndex(isIndex, isUserSelect, true);
//    }

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
        return null == name ? " " : name;
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
