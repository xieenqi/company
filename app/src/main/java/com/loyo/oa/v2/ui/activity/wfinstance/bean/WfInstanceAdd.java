package com.loyo.oa.v2.ui.activity.wfinstance.bean;

/**
 * Created by Administrator on 2014/12/30 0030.
 */
public class WfInstanceAdd {
    private String bizformId ;//int64, optional): ,
    private  String title;
    private WorkFlowValuesAdd workflowValuesAdd;
    private String wftemplateId ;//int64, optional): ,

    public WfInstanceAdd(){
        workflowValuesAdd =new WorkFlowValuesAdd();
    }

    public String getBizformId() {
        return bizformId;
    }

    public void setBizformId(String bizformId) {
        this.bizformId = bizformId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkFlowValuesAdd getWorkflowValuesAdd() {
        return workflowValuesAdd;
    }

    public void setWorkflowValuesAdd(WorkFlowValuesAdd workflowValuesAdd) {
        this.workflowValuesAdd = workflowValuesAdd;
    }

    public String getWftemplateId() {
        return wftemplateId;
    }

    public void setWftemplateId(String wftemplateId) {
        this.wftemplateId = wftemplateId;
    }
}
