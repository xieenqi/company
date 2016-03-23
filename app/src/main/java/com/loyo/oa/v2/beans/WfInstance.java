package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WfInstance extends BaseBeans implements Serializable{


    public final static int STATUS_NEW = 1;
    public final static int STATUS_PROCESSING = 2;
    public final static int STATUS_ABORT = 3;
    public final static int STATUS_APPROVED = 4;
    public final static int STATUS_FINISHED = 5;

//            New                 //新发起 流程可以被删除
//    Processing          //进行中
//            Abort               //中途审批不通过
//    Approved            //审批通过
//            Finished            //经办完成

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
    public boolean ack;
    public Demand demand;
    public ProjectInfoName ProjectInfo;

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
        public int actualNum;
        public int actualPrice;
        public int estimatedNum;
        public int estimatedPrice;
        public String memo;
        public String unit;
        public String productName;
        public String customerName;

    }

    public class ProjectInfoName implements Serializable{
        public String title;

    }
}
