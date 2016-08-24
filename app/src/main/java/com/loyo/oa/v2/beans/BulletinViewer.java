package com.loyo.oa.v2.beans;

import android.text.TextUtils;

/**
 * com.loyo.oa.v2.beans
 * 描述 :通知参与者
 * 作者 : ykb
 * 时间 : 15/8/28.
 */
public class BulletinViewer extends BaseBeans {
    private String deptId;
    private String id;
    private String userId;

    public BulletinViewer(String deptId, String userId) {
        if (!TextUtils.isEmpty(deptId)) {
            this.deptId = deptId;
        }

        if (!TextUtils.isEmpty(userId)) {
            this.userId = userId;
        }
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String getOrderStr() {
        return null;
    }

    @Override
    public String getId() {
        return id;
    }
}
