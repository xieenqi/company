package com.loyo.oa.v2.permission;

/**
 * Created by EthanGong on 2016/12/1.
 */

public enum CustomerAction {

    PREVIEW(BusinessOperation.CUSTOMER_MANAGEMENT),                    /* 查看 */
    EDIT(BusinessOperation.CUSTOMER_MANAGEMENT),                       /* 编辑 */
    PARTICIPATED_PERSON_CHANGE(BusinessOperation.CUSTOMER_MANAGEMENT), /* 修改参与人 */
    CONTACT_ADD(BusinessOperation.CUSTOMER_MANAGEMENT),                /* 添加联系人 */
    ATTACHMENT_ADD(BusinessOperation.CUSTOMER_MANAGEMENT),             /* 添加附件 */

    FOLLOWUP_ADD(BusinessOperation.VISIT_TIMELINE),        /* 添加跟进 */
    SALE_OPPORTUNITY_ADD(BusinessOperation.SALE_OPPORTUNITY), /* 添加销售机会 */
    ORDER_ADD(BusinessOperation.ORDER_MANAGEMENT),         /* 添加订单 */
    TASK_ADD(BusinessOperation.TASK),                      /* 添加任务 */
    APPROVAL_ADD(BusinessOperation.APPROVAL_PROCESS),      /* 添加审批 */

    REMINDER_ADD(BusinessOperation.CUSTOMER_MANAGEMENT),   /* TODO: 添加提醒 */
    VISIT(BusinessOperation.CUSTOMER_VISIT),               /* 拜访 */

    PICK_IN(BusinessOperation.CUSTOMER_PICKING),           /* 公海挑入 */
    DUMP(BusinessOperation.CUSTOMER_DUMPING),              /* 投公海 */
    IMPORT(BusinessOperation.CUSTOMER_MANAGEMENT),         /* 导入, 移动端无，默认实现开启 */
    EXPORT(BusinessOperation.CUSTOMER_MANAGEMENT),         /* 导出, 移动端无，默认实现开启 */
    RESPONSIBLE_PERSON_CHANGE(BusinessOperation.RESPONSIBLE_PERSON_CHANGING),
                                                           /* 修改负责人 */
    DELETE(BusinessOperation.CUSTOMER_DELETING);           /* 删除 */

    /* 操作对应的业务开关权限, Permission中读取 */
    public @BusinessOperation.Type String bizOp;

    CustomerAction(@BusinessOperation.Type String bizOp) {
        this.bizOp = bizOp;
    }
}
