package com.loyo.oa.v2.beans;

/**
 * Created by Administrator on 2014/12/31.
 */
public class WfTplNodes {
    private  String  createdAt ;//&{time Time}, optional): ,

    private  User  creator ;//&{organization User}, optional): ,

    private  int   executionDeptId ;//int, optional): ,

    private  int   executionUserId ;//int, optional): ,

    private  long  id ;//int64, optional): ,

    private  int  operatorNode ;//int, optional): ,

    private  long  roleId ;//int64, optional): ,

    private  long   sequence ;//int64, optional): ,

    private  long   wftplId ;//int64, optional):

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getExecutionDeptId() {
        return executionDeptId;
    }

    public void setExecutionDeptId(int executionDeptId) {
        this.executionDeptId = executionDeptId;
    }

    public int getExecutionUserId() {
        return executionUserId;
    }

    public void setExecutionUserId(int executionUserId) {
        this.executionUserId = executionUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
