package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activity.commonview.SelectUserHelper;
import com.loyo.oa.v2.application.MainApp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable, SelectUserHelper.SelectUserBase {

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
    private String sortLetters;

    public static String imageUrl;
    public Role role;
    public Position shortPosition;
    public Department shortDept;
    public ArrayList<UserInfo> depts = new ArrayList<>();
    public Permission permission;
    public boolean isBQQ;
    public boolean index;
    public boolean isSuperUser;
    public int gender;
    public long updatedAt;
    public long createdAt;

//    // 监听用户被选中时
//    private List<SelectUserHelper.SelectUserCallback> userCallbacks = new ArrayList<>();
//
//    public void addSelectUserCallback(SelectUserHelper.SelectUserCallback mSelectUserCallback) {
//        this.userCallbacks.add(mSelectUserCallback);
//    }
//
//    public List<SelectUserHelper.SelectUserCallback> getUserCallbacks() {
//        return userCallbacks;
//    }
//
//    public void addAllSelectUserCallback(List<SelectUserHelper.SelectUserCallback> callbacks) {
//        this.userCallbacks.addAll(callbacks);
//    }

    public boolean isSuperUser() {
        return isSuperUser;
    }

    public void setIsSuperUser(boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    public ArrayList<UserInfo> getDepts() {
        return depts;
    }

    public void setDepts(ArrayList<UserInfo> depts) {
        this.depts = depts;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {//是否选中
        this.index = index;
    }

//    public void setIndex(boolean index) {//是否选中
//        setIndex(index, true);
//    }
//
//    /**
//     * 设置选中状态, 用于部门全选
//     *
//     * @param index
//     * @param notify 是否回调给所属部门
//     */
//    public void setIndex(boolean index, boolean notify) {//是否选中
//        this.index = index;
//        for (int i = 0; i < userCallbacks.size(); i++) {
//            SelectUserHelper.SelectUserCallback callback = userCallbacks.get(i);
//            if (callback != null) {
//                callback.onSelectUser(this, notify);
//            }
//        }
//    }

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


    @Override
    public int getUserCount() {
        return 0;
    }

    @Override
    public boolean isDepart() {
        return false;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getDepartId() {
        if (depts == null)
            return null;
        return this.depts.get(0).getShortDept().getId();
    }

    public boolean isExistDepartment(String id) {
        if (TextUtils.isEmpty(id))
            return false;
        for (int i = 0; i < depts.size(); i++) {
            UserInfo info = depts.get(i);
            if (id.equals(info.getShortDept().getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equalsId(String id) {
        if (TextUtils.isEmpty(id))
            return false;
        return id.equals(this.id);
    }

    @Override
    public String getName() {
        return realname;
    }

    @Override
    public String getAvater() {
        return getAvatar();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
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

    @Override
    public int hashCode() {
        return super.hashCode();
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
