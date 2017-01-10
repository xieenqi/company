package com.loyo.oa.v2.order.permission;

/**
 * Created by EthanGong on 2017/1/9.
 */

public enum OrderAuthority {
    RESPONSIBLE_PERSON_LEVEL,   /* 负责人等级权限，最高权限*/
    TEAM_LEVEL,                 /* 团队等级权限，受限权限 */
    INVOLVED_VISITOR_LEVEL,     /* 业务关联人员等级，只能查看 */
    NONE_AUTHORITY_LEVEL;       /* 无查看权限，直接提示并退出 */
}
