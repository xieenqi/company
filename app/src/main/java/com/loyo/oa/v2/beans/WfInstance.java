package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WfInstance extends BaseBeans implements Serializable {


    public static final int STATUS_NEW = 1;
    public static final int STATUS_PROCESSING = 2;
    public static final int STATUS_ABORT = 3;
    public static final int STATUS_APPROVED = 4;
    public static final int STATUS_FINISHED = 5;

    public String id;//int64, optional): ,
    public String wftemplateId;//int64, optional): ,
    public String title;//string, optional): ,
    public ArrayList<HashMap<String, Object>> workflowValues;//WorkFlowValues, optional):
    public ArrayList<WfNodes> workflowNodes;//array[WfNodes], optional): ,
    public User nextExecutor;
    public String attachmentUUId;//string, optional): ,
    public User creator;//&{organization User}, optional): ,
    public long createdAt;
    public int serverTime;
    public int updatedAt;
    public int status;//int, optional):
    public String memo;
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public BizForm bizForm;//&{bizform BizForm}, optional): ,
    public BizExtData bizExtData;
    public boolean viewed;
    public Demand demand;
    public ProjectInfoName ProjectInfo;

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    @Override
    String getOrderStr() {
        return status + "";
    }

    @Override
    public String getOrderStr2() {
        return status + "";
    }

    @Override
    public String getId() {
        return id;
    }

    public class Demand {
        public float actualNum;
        public float actualPrice;
        public float estimatedNum;
        public float estimatedPrice;
        public String memo;
        public String unit;
        public String productName;
        public String customerName;

    }

    public class ProjectInfoName implements Serializable {
        public String title;

    }
}
