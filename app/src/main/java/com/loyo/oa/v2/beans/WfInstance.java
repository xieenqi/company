package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfNodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WfInstance extends BaseBeans implements Serializable {

    public static final int STATUS_NEW = 1;
    public static final int STATUS_PROCESSING = 2;
    public static final int STATUS_ABORT = 3;
    public static final int STATUS_APPROVED = 4;
    public static final int STATUS_FINISHED = 5;

    public int errcode;
    public String errmsg;
    public long createdAt;
    public int updatedAt;
    public int status;
    public String id;
    public String memo;
    public String title;
    public String attachmentUUId;
    public String wftemplateId;
    public ArrayList<HashMap<String, Object>> workflowValues;
    public ArrayList<WfNodes> workflowNodes;//审批节点
    public User creator;//&{organization User}, optional): ,
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public BizForm bizForm;//&{bizform BizForm}, optional): ,
    public BizExtData bizExtData;
    public Demand demand;//购买意向
    public ProjectInfoName ProjectInfo;
    public String customerId;
    public String customerName;
    public SaleDetails chance;//销售机会
    public boolean viewed;
    public OrderDetail order;
    public ArrayList<EstimateAdd> paymentRecord;

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    @Override
    public String getOrderStr() {
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
        public String id;
    }
}
