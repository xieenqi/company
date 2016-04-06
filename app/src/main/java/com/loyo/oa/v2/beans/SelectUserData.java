package com.loyo.oa.v2.beans;

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

    private boolean isSelect;
    private List<OnUserChangeCallback> mUserChangeCallback = new ArrayList<>();

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
     * @param call
     */
    public final void setCallbackSelect(boolean isSelect, OnDepChangeCallback call) {
        if (isSelect == this.isSelect) {
            return;
        }
        this.isSelect = isSelect;
        if (mUserChangeCallback != null && mUserChangeCallback.size() > 0) {
            List<SelectUserData> changeDatas = new ArrayList<>();
            for (OnUserChangeCallback callback :
                    mUserChangeCallback) {
                SelectUserData d = callback.onUserChange(isSelect);
                if (d != null) {
                    changeDatas.add(d);
                }
            }
            if (call != null) {
                call.onDepChange(changeDatas);
            }
        }
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
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
        return xpath;
    }

    public final void setXpath(String xpath) {
        this.xpath = xpath;
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
                '}';
    }

    public interface OnDepChangeCallback {
        void onDepChange(List<SelectUserData> datas);
    }

    public interface OnUserChangeCallback {
        SelectUserData onUserChange(boolean isSelect);
    }
}
