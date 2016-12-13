package com.loyo.oa.v2.permission;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by EthanGong on 2016/11/28.
 */

public class BusinessOperation {
    public final static String DEFAULT               = "0";     /* 缺省 */
    public final static String ANNOUNCEMENT          = "0200";  /* 公告通知 */
    public final static String PROJECT_MANAGEMENT    = "0201";  /* 项目管理 */
    public final static String TASK                  = "0202";  /* 任务计划 */
    public final static String WORK_REPORT           = "0203";  /* 工作报告 */
    public final static String APPROVAL_PROCESS      = "0204";  /* 审批流程 */
    public final static String CUSTOMER_MANAGEMENT   = "0205";  /* 客户管理 */
    public final static String AUGMENTER_STOCK       = "0207";  /* 增量/存量 */
    public final static String FOLLOWUP_STATISTICS   = "0208";  /* 跟进统计 */
    public final static String RESULTS_LOOK          = "0209";  /* 业绩看板 */
    // 0210    /* 产品管理 */
    public final static String ATTENDANCE_MANAGEMENT = "0211";  /* 考勤管理 */
    public final static String LOCATION_TRACKING     = "0212";  /* 轨迹定位 */
    public final static String ORGANIZATION_CONTACTS = "0213";  /* 通讯录 */
    public final static String SALE_OPPORTUNITY      = "0215";  /* 销售机会 */
    public final static String ORDER_MANAGEMENT      = "0216";  /* 订单管理 */
    public final static String CLUE_MANAGEMENT       = "0217";  /* 线索管理 */
    public final static String WORKSHEET_MANAGEMENT  = "0218";  /* 工单管理 */

    // 0219    /* 聊天记录 */
    // 0223    /* 销量排行 */
    public final static String CUSTOMER_VISIT        = "0228";  /* 客户拜访 */
    public final static String VISIT_TIMELINE        = "0229";  /* 跟进动态 */
    // 0242    /* 审批设置 */
    // 0243    /* 考勤定位 */
    // 0303    /* 任务统计 */
    // 0305    /* 报告统计 */
    // 0307    /* 审批统计 */
    // 0318    /* 考勤统计 */
    // 0319    /* 角色权限 */
    // 0320    /* 组织架构 */
    // 0324    /* CRM设置 */
    // 0326    /* 公司信息 */
    public final static String PROJECT_CREATING      = "0401";  /* 创建项目 */
    public final static String ANNOUNCEMENT_POSTING  = "0402";  /* 发布公告 */
    public final static String CUSTOMER_DUMPING      = "0403";  /* 投入公海 */
    public final static String CUSTOMER_PICKING      = "0404";  /* 客户挑入 */
    public final static String CUSTOMER_DELETING     = "0405";  /* 客户删除/客户回收站 */
    // 0406    /* 客户导入 */
    // 0407    /* 客户导出 */
    // 0408    /* 产品编辑 */
    public final static String CLUE_DELETING         = "0409";  /* 线索删除 */

    public final static String RESPONSIBLE_PERSON_CHANGING = "0410";  /* 更改负责人 */

    @StringDef({
            DEFAULT,
            ANNOUNCEMENT,          PROJECT_MANAGEMENT,          TASK,                  WORK_REPORT,
            APPROVAL_PROCESS,      CUSTOMER_MANAGEMENT,         AUGMENTER_STOCK,       FOLLOWUP_STATISTICS,
            RESULTS_LOOK,          ATTENDANCE_MANAGEMENT,       LOCATION_TRACKING,
            ORGANIZATION_CONTACTS, SALE_OPPORTUNITY,            ORDER_MANAGEMENT,      CLUE_MANAGEMENT,
            WORKSHEET_MANAGEMENT,  CUSTOMER_VISIT,              VISIT_TIMELINE,        PROJECT_CREATING,
            ANNOUNCEMENT_POSTING,  CUSTOMER_DUMPING,            CUSTOMER_PICKING,      CUSTOMER_DELETING,
            CLUE_DELETING,         RESPONSIBLE_PERSON_CHANGING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
