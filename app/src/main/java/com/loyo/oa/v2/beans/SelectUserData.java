package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activity.commonview.SelectUserHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户选择界面用户数据封装
 */
public class SelectUserData implements Serializable {

    private String id;
    private String name;
    private String avatar;
    private String xpath;

    private String deptName; // 部门名称
    private String npcName; // 用户职称

    private boolean isSelect;
    private List<OnUserChangeCallback> mUserChangeCallback = new ArrayList<>();
    private OnDepChangeCallback mDepChangeCallback;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    /**
     * 设置用户选中状态, 并回调部门选中状态
     *
     * @param isSelect
     */
    public final boolean setCallbackSelect(boolean isSelect) {
        if (isSelect == this.isSelect) {
            return false;
        }
        this.isSelect = isSelect;
        if (mUserChangeCallback != null && mUserChangeCallback.size() > 0) {
            List<SelectDepData> changeDepDatas = new ArrayList<>();
            for (OnUserChangeCallback callback :
                    mUserChangeCallback) {
                SelectDepData changeDep = callback.onUserChange(isSelect, SelectUserData.this, false);
                if (changeDep == null) {
                    continue;
                }
                changeDepDatas.add(changeDep);
            }
            if (changeDepDatas.size() > 0 && mDepChangeCallback != null) {
//                removeSameXPath(changeDepDatas)
                checkSameXPath(changeDepDatas);
                mDepChangeCallback.onDepChange(changeDepDatas);
            }
        }
        return true;
    }

    /**
     * 设置用户选中状态, 并回调部门选中状态
     *
     * @param isSelect
     */
    public final boolean setCallbackSelect(boolean isSelect, boolean notify) {
        if (isSelect == this.isSelect) {
            return false;
        }
        this.isSelect = isSelect;
        if (mUserChangeCallback != null && mUserChangeCallback.size() > 0) {
            for (OnUserChangeCallback callback :
                    mUserChangeCallback) {
                SelectDepData changeDep = callback.onUserChange(isSelect, SelectUserData.this, true);
                if (changeDep == null) {
                    continue;
                }
                SelectUserHelper.mAllSelectDatas.add(changeDep);
            }
            if (SelectUserHelper.mAllSelectDatas.size() > 0 && mDepChangeCallback != null && notify) {
                checkSameXPath(SelectUserHelper.mAllSelectDatas);
                mDepChangeCallback.onDepChange(SelectUserHelper.mAllSelectDatas);
            }
        }
        return true;
    }

    /**
     * 检查列表中, 如果有传入选的子部门, 移除该子部门并添加,
     *
     * @param changeDatas
     * @param
     * @return
     */
    private void checkSameXPath(List<SelectDepData> changeDatas) {
        for (int i = 0; i < changeDatas.size(); i++) {
            SelectDepData data = changeDatas.get(i);
            lable:
            for (int j = 0; j < changeDatas.size(); j++) {
                SelectDepData depData = changeDatas.get(i);
                if (depData.getId().equals(data.getId())) {
                    continue lable;
                }
                if (depData.getXpath().contains(data.getXpath())) {
                    changeDatas.remove(j);
                    j--;
                }
            }
        }
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final String getAvatar() {
        return avatar;
    }

    public final void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public final String getXpath() {
        return TextUtils.isEmpty(xpath) ? "" : xpath;
    }

    public final void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public void setmDepChangeCallback(OnDepChangeCallback mDepChangeCallback) {
        this.mDepChangeCallback = mDepChangeCallback;
    }

    /**
     * 添加用户选择监听
     *
     * @param callback
     */
    public final void addUserChangeCallback(OnUserChangeCallback callback) {
        this.mUserChangeCallback.add(callback);
    }

    /**
     * 移除用户选中监听
     *
     * @param callback
     */
    public final void removeUserChangeCallback(OnUserChangeCallback callback) {
        this.mUserChangeCallback.remove(callback);
    }

    public List<OnUserChangeCallback> getmUserChangeCallback() {
        return mUserChangeCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectUserData that = (SelectUserData) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SelectUserData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", xpath='" + xpath + '\'' +
                ", deptName='" + deptName + '\'' +
                ", npcName='" + npcName + '\'' +
                ", isSelect=" + isSelect +
                ", mUserChangeCallback=" + +
                '}';
    }

    public interface OnDepChangeCallback {

        void onDepAllChange(SelectDepData data);

        void onDepChange(List<SelectDepData> datas);

        void addSelectUserItem(SelectUserData data);

        void removeSelectUserItem(SelectUserData data);
    }

    public interface OnUserChangeCallback {
        SelectDepData onUserChange(boolean isSelect, SelectUserData user, boolean isAllSelect);
    }

    public boolean isExistThis(SelectUserData data) {

        return false;
    }
}
