package com.loyo.oa.v2.permission;

/**
 * Created by EthanGong on 2016/12/1.
 */

public enum CustomerAuthority {
    ADMIN_LEVEL,   /* 超级管理员等级权限，最高权限*/
    RESPONSIBLE_PERSON_LEVEL,   /* 负责人等级权限，最高权限*/
    PARTICIPATED_PERSON_LEVEL,  /* 参与人等级权限，受限权限 */
    INVOLVED_VISITOR_LEVEL,     /* 业务关联人员等级，只能查看 */
    NONE_AUTHORITY_LEVEL;       /* 无查看权限，直接提示并退出 */
}
