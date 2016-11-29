package com.loyo.oa.v2.permission;

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

    public boolean hasTeamPermission(@BusinessOperation.Type String module) {
        


        return true;
    }

    public int dateRange(@BusinessOperation.Type String mudule) {
        return 0;
    }
}
