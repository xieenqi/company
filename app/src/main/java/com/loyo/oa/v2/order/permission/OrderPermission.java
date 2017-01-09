package com.loyo.oa.v2.order.permission;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;

import java.util.EnumSet;

import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_CAPITAL_RETURN_PLAN_CRUD;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_CAPITAL_RETURN_RECORD_CRUD;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_COPY;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_DELETE;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_EDIT;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_PREVIEW;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_RESPONSIBLE_PERSON_CHANGE;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_TERMINATE;
import static com.loyo.oa.v2.order.permission.OrderAction.ORDER_WORKSHEET_ADD;

/**
 * Created by EthanGong on 2017/1/9.
 */

public class OrderPermission {

    private static EnumSet[][] TABLE;
    /* 静态块，初始化数据 */
    {
        EnumSet[][] table = {
                {
                        /* 负责人, 待审批 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_EDIT, ORDER_COPY, ORDER_DELETE),

                        /* 负责人, 审批中 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_COPY),
                        /* 负责人, 未通过 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_EDIT, ORDER_COPY, ORDER_DELETE),
                        /* 负责人, 进行中 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_COPY,
                                ORDER_CAPITAL_RETURN_PLAN_CRUD, ORDER_CAPITAL_RETURN_RECORD_CRUD,
                                ORDER_WORKSHEET_ADD, ORDER_TERMINATE, ORDER_RESPONSIBLE_PERSON_CHANGE),
                        /* 负责人, 已完成 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_COPY,
                                ORDER_WORKSHEET_ADD, ORDER_TERMINATE, ORDER_RESPONSIBLE_PERSON_CHANGE),
                        /* 负责人, 意外终止 */
                        EnumSet.of(ORDER_PREVIEW, ORDER_COPY, ORDER_DELETE),
                },
                {
                        /* 团队, 待审批 */
                        EnumSet.of(ORDER_PREVIEW),

                        /* 团队, 审批中 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 团队, 未通过 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 团队, 进行中 */
                        EnumSet.of(ORDER_PREVIEW,
                                ORDER_WORKSHEET_ADD,
                                ORDER_RESPONSIBLE_PERSON_CHANGE),
                        /* 团队, 已完成 */
                        EnumSet.of(ORDER_PREVIEW,
                                ORDER_WORKSHEET_ADD,
                                ORDER_RESPONSIBLE_PERSON_CHANGE),
                        /* 团队, 意外终止 */
                        EnumSet.of(ORDER_PREVIEW,
                                ORDER_WORKSHEET_ADD),
                },
                {
                        /* 业务相关, 待审批 */
                        EnumSet.of(ORDER_PREVIEW),

                        /* 业务相关, 审批中 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 业务相关, 未通过 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 业务相关, 进行中 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 业务相关, 已完成 */
                        EnumSet.of(ORDER_PREVIEW),
                        /* 业务相关, 意外终止 */
                        EnumSet.of(ORDER_PREVIEW),
                }
        };
        TABLE = table;
    }


    public static boolean hasOrderAuthority(int relationState, int state, OrderAction action) {
        /* 功能模块关闭 */
        if (!PermissionManager.getInstance().hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
            return false;
        }

        /* 功能模块关闭 */
        @BusinessOperation.Type String operation = action.bizOp;
        if (!BusinessOperation.DEFAULT.equals(operation)
                && !PermissionManager.getInstance().hasPermission(operation)) {
            return false;
        }

        /* 订单状态不明 */
        if (state < OrderState.NONE.ordinal() || state > OrderState.ORDER_TERMINATED.ordinal()) { //
            return false;
        }

        OrderAuthority authorityLevel = getOrderAuthorityLevel(relationState);
        EnumSet<CustomerAction> set = TABLE[authorityLevel.ordinal()][state-1];
        if (set.contains(action)) {
            return true;
        }

        return false;
    }

    public static OrderAuthority getOrderAuthorityLevel(int relationState) {
        OrderAuthority level;
        switch(relationState) {
            case Customer.RelationResponsible:
                level = OrderAuthority.RESPONSIBLE_PERSON_LEVEL;
                break;
            case Customer.RelationParticipated:
                level = OrderAuthority.TEAM_LEVEL;
                break;
            case Customer.RelationInvolved:
                level = OrderAuthority.INVOLVED_VISITOR_LEVEL;
                break;
            default:
                level = OrderAuthority.INVOLVED_VISITOR_LEVEL;
                break;
        }
        return level;
    }
}
