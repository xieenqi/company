package com.loyo.oa.v2.activityui.wfinstance.common;

/**
 * Created by EthanGong on 2017/1/9.
 */

public class ApprovalAddBuilder {

    private static Builder sBuilder;

    private static class Builder {
        public String customerId;
        public String customerName;
        // TODO:
        // 流程类型

        public Builder() {
        }
    }

    public static void startCreate() {
        sBuilder = new Builder();
    }

    public static void endCreate() {
        sBuilder = null;
    }

    public static void setCustomer(String customerId, String customerName) {
        if (sBuilder != null) {
            sBuilder.customerId = customerId;
            sBuilder.customerName = customerName;
        }
    }

    public static String getCustomerId() {
        if (sBuilder != null) {
            return sBuilder.customerId;
        }
        return null;
    }

    public static String getCustomerName() {
        if (sBuilder != null) {
            return sBuilder.customerName;
        }
        return null;
    }

}
