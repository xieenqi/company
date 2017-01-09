package com.loyo.oa.v2.order.permission;

/**
 * Created by EthanGong on 2017/1/9.
 */

// TODO: 新增了审批中
// TODO: 和OrderStatus合并

public enum OrderState {
    NONE,
    APPROVE_WAITING,
    APPROVE_PROCESSING,
    APPROVE_DENIED,
    ORDER_PROCESSING,
    ORDER_FINISHED,
    ORDER_TERMINATED
}
