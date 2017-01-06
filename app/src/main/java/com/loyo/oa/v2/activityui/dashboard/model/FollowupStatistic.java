package com.loyo.oa.v2.activityui.dashboard.model;

/**
 * 客户和线索跟进
 * Created by yyy on 16/12/28.
 */

public class FollowupStatistic {

    public DsFowUpCustomer saleActivity;
    public DsFowUpSale salesLead;

    public class DsFowUpCustomer {
        public DivModel activity;
        public DivModel visit;
        public DivModel voice;
    }

    public class DsFowUpSale {
        public DivModel activity;
        public DivModel voice;
    }

    public class DivModel {
        public String name;
        public int totalCount;
        public int distinctCount;
    }

    public String getActivityTotal() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.activity.totalCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getActivityDistinct() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.activity.distinctCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getVisitTotal() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.visit.totalCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getVisitDistinct() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.visit.distinctCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getVoiceTotal() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.voice.totalCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getVoiceDistinct() {
        String result= "";
        try {
            result = String.valueOf(saleActivity.voice.distinctCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getClueActivityTotal() {
        String result= "";
        try {
            result = String.valueOf(salesLead.activity.totalCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getClueActivityDistinct() {
        String result= "";
        try {
            result = String.valueOf(salesLead.activity.distinctCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getClueVoiceTotal() {
        String result= "";
        try {
            result = String.valueOf(salesLead.voice.totalCount);
        }
        catch (Exception e) {
        }
        return result;
    }

    public String getClueVoiceDistinct() {
        String result= "";
        try {
            result = String.valueOf(salesLead.voice.distinctCount);
        }
        catch (Exception e) {
        }
        return result;
    }
}
