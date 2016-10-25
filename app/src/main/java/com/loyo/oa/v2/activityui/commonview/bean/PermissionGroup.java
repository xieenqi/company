package com.loyo.oa.v2.activityui.commonview.bean;

import com.loyo.oa.v2.beans.Permission;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 个人信息中的 权限数据
 * Created by xeq on 16/10/19.
 */

public class PermissionGroup implements Serializable {
    public ArrayList<Permission> topModules;
    public ArrayList<Permission> subModules;
}
