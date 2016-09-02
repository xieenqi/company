package com.loyo.oa.v2.activityui.worksheet.common;

import java.io.Serializable;

/**
 * Created by EthanGong on 16/8/30.
 * <p/>
 * 工单列表的类型
 */
public enum WorksheetListType implements Serializable {
    SELF_CREATED,  /* 我创建的工单 */
    ASSIGNABLE,  /* 我分派的工单 */
    RESPONSABLE,  /* 我负责的工单 */
    TEAM;   /* 团队的工单 */
}
