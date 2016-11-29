package com.loyo.oa.v2.permission;

import com.loyo.oa.v2.application.MainApp;

import java.util.HashMap;

/**
 * Created by EthanGong on 2016/11/28.
 */
public class PermissionManager {
    private static PermissionManager ourInstance = new PermissionManager();

    public static PermissionManager getInstance() {
        return ourInstance;
    }

    private HashMap<String, Permission> data = new HashMap<>();

    private PermissionManager() {
    }

    public PermissionManager init(HashMap<String, Permission> map) {
        if (map != null) {
            data = map;
        }
        return this;
    }

    public boolean hasPermission(@BusinessOperation.Type String module) {

        if (hasSuperPriority()) {
            return true;
        }

        Permission permission = data.get(module);
        if (permission == null || ! permission.isEnable()) {
            return false;
        }

        return true;
    }

    public boolean teamPermission(@BusinessOperation.Type String module) {
        if (hasSuperPriority()) {
            return true;
        }

        Permission permission = data.get(module);
        if (permission == null || ! permission.isEnable()) {
            return false;
        }
        else if (permission.dataRange >= Permission.PERSONAL) {
            return false;
        }

        return true;
    }

    public int dataRange(@BusinessOperation.Type String mudule) {

        if (hasSuperPriority()) {
            return Permission.COMPANY;
        }

        Permission permission = data.get(mudule);
        if (permission == null) {
            return Permission.NONE;
        }
        else {
            return permission.dataRange;
        }
    }

    /* 超级管理员权限 */
    public boolean hasSuperPriority() {
        if (MainApp.user != null) {
            return MainApp.user.isSuperUser;
        }
        return false;
    }
}
