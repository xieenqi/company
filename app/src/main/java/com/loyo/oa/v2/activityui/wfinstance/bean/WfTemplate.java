package com.loyo.oa.v2.activityui.wfinstance.bean;

import com.loyo.oa.v2.activityui.other.bean.User;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/31.
 */
public class WfTemplate implements Serializable{
    private  String  bizformId ;//int64, optional): ,

    private  String  companyId ;//int, optional): ,

    private  long  createdAt ;//&{time Time}, optional): ,

    private User creator ;//&{organization User}, optional): ,

    private  boolean  enable ;//bool, optional): ,

    private  String   id ;//int64, optional): ,

    private  String    title ;//string, optional): ,

    private ArrayList<WfTplNodes> wfTplNodes ;//array[WfTplNodes], optional):

    public String getBizformId() {
        return bizformId;
    }

    public void setBizformId(String bizformId) {
        this.bizformId = bizformId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<WfTplNodes> getWfTplNodes() {
        return wfTplNodes;
    }

    public void setWfTplNodes(ArrayList<WfTplNodes> wfTplNodes) {
        this.wfTplNodes = wfTplNodes;
    }
}
