package com.loyo.oa.v2.activityui.wfinstance.bean;

import com.loyo.oa.v2.activityui.other.bean.User;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/31.
 */
public class WfTplNodes implements Serializable{
    private  int  createdAt ;//&{time Time}, optional): ,

    private User creator ;//&{organization User}, optional): ,

    private  String   executionDeptId ;//int, optional): ,

    private  String   executionUserId ;//int, optional): ,

    private  String  id ;//int64, optional): ,

    private  int  operatorNode ;//int, optional): ,

    private  long  roleId ;//int64, optional): ,

    private  long   sequence ;//int64, optional): ,

    private  long   wftplId ;//int64, optional):

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getExecutionDeptId() {
        return executionDeptId;
    }

    public void setExecutionDeptId(String executionDeptId) {
        this.executionDeptId = executionDeptId;
    }

    public String getExecutionUserId() {
        return executionUserId;
    }

    public void setExecutionUserId(String executionUserId) {
        this.executionUserId = executionUserId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOperatorNode() {
        return operatorNode;
    }

    public void setOperatorNode(int operatorNode) {
        this.operatorNode = operatorNode;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getWftplId() {
        return wftplId;
    }

    public void setWftplId(long wftplId) {
        this.wftplId = wftplId;
    }
}
