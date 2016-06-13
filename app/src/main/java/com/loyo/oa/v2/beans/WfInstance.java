package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activity.sale.bean.SaleDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WfInstance extends BaseBeans implements Serializable {


    public static final int STATUS_NEW = 1;
    public static final int STATUS_PROCESSING = 2;
    public static final int STATUS_ABORT = 3;
    public static final int STATUS_APPROVED = 4;
    public static final int STATUS_FINISHED = 5;

    public String wftemplateId;//int64, optional): ,
    public ArrayList<HashMap<String, Object>> workflowValues;//WorkFlowValues, optional):
    public ArrayList<WfNodes> workflowNodes;//array[WfNodes], optional): ,
    public User nextExecutor;
    public String attachmentUUId;//string, optional): ,
    public User creator;//&{organization User}, optional): ,
    public long createdAt;
    public int serverTime;
    public int updatedAt;
    public String memo;
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public BizForm bizForm;//&{bizform BizForm}, optional): ,
    public BizExtData bizExtData;

    public Demand demand;//购买意向
    public ProjectInfoName ProjectInfo;

    public SaleDetails chance;//销售机会

    //精简之后的数据
    public String title;
    public long created_at;
    public String next_executor_name;
    public int status;
    public boolean viewed;
    public String id;

    //    "id": "57578f31526f151a9908d9cb",
//            "title": "newadmin提交2审批",
//            "created_at": 1465356081,
//            "next_executor_name": "wsy测试账号",
//            "status": 1
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
        public String id;
    }
}
