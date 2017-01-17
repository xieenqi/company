package com.loyo.oa.v2.order.permission;

import com.loyo.oa.v2.permission.BusinessOperation;

/**
 * Created by EthanGong on 2016/12/1.
 */

public enum OrderAction {

    ORDER_PREVIEW(BusinessOperation.ORDER_MANAGEMENT),                    /* 查看 */
    ORDER_EDIT(BusinessOperation.ORDER_MANAGEMENT),                       /* 编辑 */
    ORDER_COPY(BusinessOperation.ORDER_MANAGEMENT),                       /* 编辑 */
    ORDER_DELETE(BusinessOperation.ORDER_MANAGEMENT),                     /* 删除 */
    ORDER_TERMINATE(BusinessOperation.ORDER_MANAGEMENT),                  /* 意外终止 */
    ORDER_RESPONSIBLE_PERSON_CHANGE(BusinessOperation.ORDER_RESPONSIBLE_PERSON_CHANGING),  /* 修改负责人 */

    ORDER_CAPITAL_RETURN_PLAN_CRUD(BusinessOperation.ORDER_MANAGEMENT),   /* 增删改回款计划 */
    ORDER_CAPITAL_RETURN_RECORD_CRUD(BusinessOperation.ORDER_MANAGEMENT), /* 增删改回款记录 */
    ORDER_WORKSHEET_ADD(BusinessOperation.ORDER_MANAGEMENT),              /* 添加工单 */

    ORDER_IMPORT(BusinessOperation.ORDER_MANAGEMENT),         /* 导入, 移动端无，默认实现开启 */
    ORDER_EXPORT(BusinessOperation.ORDER_MANAGEMENT),         /* 导出, 移动端无，默认实现开启 */
    ;

    /* 操作对应的业务开关权限, Permission中读取 */
    public @BusinessOperation.Type String bizOp;

    OrderAction(@BusinessOperation.Type String bizOp) {
        this.bizOp = bizOp;
    }
}
