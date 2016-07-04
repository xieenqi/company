package com.loyo.oa.v2.activityui.other.bean;

import com.loyo.oa.v2.activityui.commonview.SelectUserHelper;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户选择及界面部门数据封装
 */
public class SelectDepData extends SelectUserData implements Serializable {
    private String id;
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
            user.setAllCallbackSelect(isSelect);
        }
        if (SelectUserHelper.mAllSelectDatas.size() > 0 && mDepChangeCallback != null) {
            checkSameXPath(SelectUserHelper.mAllSelectDatas);
            mDepChangeCallback.onDepChange(SelectUserHelper.mAllSelectDatas);
        }
    }

    public void setmDepChangeCallback(OnDepChangeCallback mDepChangeCallback) {
        this.mDepChangeCallback = mDepChangeCallback;
        if (users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                users.get(i).setmDepChangeCallback(mDepChangeCallback);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SelectDepData depData = (SelectDepData) o;

        return !(id != null ? !id.equals(depData.id) : depData.id != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
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
    public String toString() {
        return "SelectDepData{" +
                ", mSelectCount=" + mSelectCount +
                ", mDepChangeCallback=" + mDepChangeCallback +
                ", mUserChangeCallback=" + mUserChangeCallback +
                '}';
    }


}
