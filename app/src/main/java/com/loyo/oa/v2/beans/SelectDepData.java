package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户选择及界面部门数据封装
 */
public class SelectDepData extends SelectUserData implements Serializable {
//    private String id;
//    private String name;
//    private String avatar;
//    private String xpath;

    private List<SelectUserData> users = new ArrayList<>(); // 选择的用户

    private int mSelectCount = 0; // 当前已选择的用户数量

    private OnUserChangeCallback mUserChangeCallback = new OnUserChangeCallback() {
        @Override
        public SelectDepData onUserChange(boolean isSelect) {
            if (isSelect) {
                addSelectCount();
            } else {
                minusSelectCount();
            }
            return SelectDepData.this;
        }
    };

    /**
     * 添加被选中的数量
     */
    public void addSelectCount() {
        mSelectCount = mSelectCount + 1 > users.size() ? users.size() : mSelectCount + 1;
    }

    /**
     * 减少被选中的数量
     */
    public void minusSelectCount() {
        mSelectCount = mSelectCount - 1 < 0 ? 0 : mSelectCount - 1;
    }

    @Override
    public boolean isSelect() {
        if (users == null || users.size() == 0) {
            return false;
        }
        return users.size() == mSelectCount /*|| isSelect()*/;
    }

    public void setAllSelect(boolean isSelect) {
        for (SelectUserData user :
                users) {
            user.setCallbackSelect(isSelect, null);
        }
    }

    public List<SelectUserData> getUsers() {
        return users;
    }

    public void setUsers(List<SelectUserData> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return super.toString() + " ::: SelectDepData{" +
                "users=" + users +
                ", mSelectCount=" + mSelectCount +
                '}';
    }
}
