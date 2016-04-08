package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activity.commonview.SelectUserHelper;
import com.loyo.oa.v2.tool.LogUtil;

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

    public int mSelectCount = 0; // 当前已选择的用户数量

    private OnDepChangeCallback mDepChangeCallback;

    public OnUserChangeCallback mUserChangeCallback = new OnUserChangeCallback() {
        @Override
        public SelectDepData onUserChange(boolean isSelect, SelectUserData user, boolean isAllSelect) {
            boolean oldSelect = isSelect();
            if (isSelect) {
                addSelectCount();
            } else {
                minusSelectCount();
            }
            LogUtil.err(getName() + "; count = " + SelectDepData.this.mSelectCount);
            if ((isSelect() && !oldSelect) || (oldSelect && !isSelect()) || mSelectCount == 0) {
//                    mDepChangeCallback.onDepChange(SelectDepData.this, oldSelect);
                return SelectDepData.this;
            }
            if (mDepChangeCallback != null) {
                if (!isAllSelect) {
                    if (isSelect) {
                        mDepChangeCallback.addSelectUserItem(user);
                    } else {
                        mDepChangeCallback.removeSelectUserItem(user);
                    }
                }
            }
            return null;
        }
    };

    public void startCallback() {
        for (SelectUserData user :
                users) {
            user.addUserChangeCallback(mUserChangeCallback);
            if (mDepChangeCallback != null) {
                user.setmDepChangeCallback(mDepChangeCallback);
            }
        }
    }

    private SelectUserData getUserById(String id) {
        for (SelectUserData data :
                users) {
            if (id.equals(data.getId()))
                return data;
        }
        return null;
    }

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

    /**
     * 部门全选
     *
     * @param isSelect
     */
    public void setAllSelect(final boolean isSelect) {
        SelectUserHelper.mAllSelectDatas.clear();
        for (int i = 0; i < users.size(); i++) {
            SelectUserData user = users.get(i);
            user.setCallbackSelect(isSelect, i == users.size() - 1);
        }
//        mSelectCount = isSelect ? users.size() : 0; // 刷新当前部门的选中数量
//        List<SelectDepData> datas = new ArrayList<>();
//        if (!TextUtils.isEmpty(getXpath()))
//            for (int i = 0; i < SelectUserHelper.mSelectDatas.size(); i++) {
//                SelectDepData data = SelectUserHelper.mSelectDatas.get(i);
//                if (data.getXpath().contains(getXpath())
//                        && !getId().equals(data.getId())) { // 刷新子部门的的选中数量
//
//                    data.mSelectCount = isSelect ? data.getUsers().size() : 0;
//                } else if (getXpath().contains(data.getXpath())
//                        && !getId().equals(data.getId())) { // 刷新父部门选中的数量
//
//                    data.refreshSelectCount();
//                }
//            }
//        if (mDepChangeCallback != null) {
//            mDepChangeCallback.onDepAllChange(this);
//        }
    }

    public void setmDepChangeCallback(OnDepChangeCallback mDepChangeCallback) {
        this.mDepChangeCallback = mDepChangeCallback;
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                users.get(i).setmDepChangeCallback(mDepChangeCallback);
            }
        }
    }

    /**
     * 刷新选中的数量
     */
    public SelectDepData refreshSelectCount() {
        int count = 0;
        for (int i = 0; i < getUsers().size(); i++) {
            if (users.get(i).isSelect()) {
                count++;
            }
        }
        mSelectCount = count;
        return this;
    }

    public List<SelectUserData> getUsers() {
        return users;
    }

    public void setUsers(List<SelectUserData> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "SelectDepData{" +
                ", mSelectCount=" + mSelectCount +
                ", mDepChangeCallback=" + mDepChangeCallback +
                ", mUserChangeCallback=" + mUserChangeCallback +
                '}';
    }


}
