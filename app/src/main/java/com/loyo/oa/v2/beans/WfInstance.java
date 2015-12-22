package com.loyo.oa.v2.beans;

import java.util.ArrayList;
import java.util.HashMap;

public class WfInstance extends BaseBeans {


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
    public String bizformId;//int64, optional): ,
    public String bizformName;
    public String wftemplateId;//int64, optional): ,
    public String templateName;
    public String title;//string, optional): ,
    public String serialNumber;//string, optional): ,
    public boolean topFlag;//bool, optional): ,
    public ArrayList<HashMap<String, Object>> workflowValues;//WorkFlowValues, optional):
    public ArrayList<WfNodes> workflowNodes;//array[WfNodes], optional): ,
    public User nextExecutor;
    public String attachmentUUId;//string, optional): ,
    public User creator;//&{organization User}, optional): ,
    public long createdAt;
    public int updatedAt;
    public String deptId;
    public int status;//int, optional):
    public String memo;
    public int nextExecutorId;//int, optional): ,
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public BizForm bizform;//&{bizform BizForm}, optional): ,

    public boolean ack;
    public boolean candel;

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

    //    public String getDeptId() {
//        return deptId;
//    }
//
//    public void setDeptId(String deptId) {
//        this.deptId = deptId;
//    }

//    public User getNextExecutor() {
//        return nextExecutor;
//    }
//
//    public void setNextExecutor(User nextExecutor) {
//        this.nextExecutor = nextExecutor;
//    }
//
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//    public boolean isAck() {
//        return ack;
//    }
//
//    public void setAck(boolean ack) {
//        this.ack = ack;
//    }
//
//    public String getAttachmentUUId() {
//        return attachmentUUId;
//    }
//
//    public void setAttachmentUUId(String attachmentUUId) {
//        this.attachmentUUId = attachmentUUId;
//    }
//
//    public ArrayList<Attachment> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(ArrayList<Attachment> attachments) {
//        this.attachments = attachments;
//    }
//
//    public BizForm getBizform() {
//        return bizform;
//    }
//
//    public void setBizform(BizForm bizform) {
//        this.bizform = bizform;
//    }
//
//    public String getBizformId() {
//        return bizformId;
//    }
//
//    public void setBizformId(String bizformId) {
//        this.bizformId = bizformId;
//    }
//
//    public long getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(long createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public User getCreator() {
//        return creator;
//    }
//
//    public void setCreator(User creator) {
//        this.creator = creator;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public int getNextExecutorId() {
//        return nextExecutorId;
//    }
//
//    public void setNextExecutorId(int nextExecutorId) {
//        this.nextExecutorId = nextExecutorId;
//    }
//
//    public String getSerialNumber() {
//        return serialNumber;
//    }
//
//    public void setSerialNumber(String serialNumber) {
//        this.serialNumber = serialNumber;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public boolean isTopFlag() {
//        return topFlag;
//    }
//
//    public void setTopFlag(boolean topFlag) {
//        this.topFlag = topFlag;
//    }
//
//    public String getWftemplateId() {
//        return wftemplateId;
//    }
//
//    public void setWftemplateId(String wftemplateId) {
//        this.wftemplateId = wftemplateId;
//    }
//
//    public ArrayList<WfNodes> getWorkflowNodes() {
//        return workflowNodes;
//    }
//
//    public void setWorkflowNodes(ArrayList<WfNodes> workflowNodes) {
//        this.workflowNodes = workflowNodes;
//    }
//
//    public ArrayList<HashMap<String, Object>> getWorkflowValues() {
//        return workflowValues;
//    }
//
//    public void setWorkflowValues(ArrayList<HashMap<String, Object>> workflowValues) {
//        this.workflowValues = workflowValues;
//    }
//
//
}
