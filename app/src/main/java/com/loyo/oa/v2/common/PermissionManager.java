package com.loyo.oa.v2.common;

import com.loyo.oa.v2.beans.Permission;

import java.util.HashMap;

/**
 * 用于处理权限相关数据
 * Created by xeq on 16/10/25.
 */
//TODO  最终数据库保存权限数据
public class PermissionManager {
    private static PermissionManager permissionManager;

    public static PermissionManager getInstance() {
        if (permissionManager == null)
            permissionManager = new PermissionManager();
        return permissionManager;
    }

    /**
     * 获取权限数据保存权限数据
     */
    public void setPermissionData(HashMap<String, Permission> map) {

    }

    public void getPermissionData() {

    }
}
