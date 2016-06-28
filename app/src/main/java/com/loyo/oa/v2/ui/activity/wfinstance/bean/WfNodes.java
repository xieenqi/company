package com.loyo.oa.v2.ui.activity.wfinstance.bean;

import com.loyo.oa.v2.ui.activity.other.bean.User;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/29 0029.
 */
public class WfNodes implements Serializable {
    public static final int ACTIVE_STATUS_WAIT=1;
    public static final int ACTIVE_STATUS_PROCESSING=2;
    public static final int ACTIVE_STATUS_PROCESSED=3;

    private int handAt;
    private int  remindAt;
    private int active ;//bool, optional): ,
    private boolean approveFlag ;//bool, optional): ,
    private boolean isOverTime;
    private String comment ;//string, optional): ,
    private long createdAt ;//&{time Time}, optional): ,
    private String executor ;//int, optional): ,
    private User executorUser ;//&{organization User}, optional): ,
    private String id ;//int64, optional): ,
    private boolean needApprove ;//bool, optional): ,
    private long sequence ;//int64, optional): ,
    private long updateAt ;//&{time Time}, optional): ,
    private String wfInstanceId ;//int64, optional):
    private boolean approveResult;


    public boolean isOverTime() {
        return isOverTime;
    }

    public void setIsOverTime(boolean isOverTime) {
        this.isOverTime = isOverTime;
    }

    public int getHandAt() {
        return handAt;
    }

    public void setHandAt(int handAt) {
        this.handAt = handAt;
    }

    public int getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(int remindAt) {
        this.remindAt = remindAt;
    }

    public boolean isApproveResult() {
        return approveResult;
    }

    public void setApproveResult(boolean approveResult) {
        this.approveResult = approveResult;
    }

    public boolean isActive() {
        return active==ACTIVE_STATUS_PROCESSED;
    }

    public int getActive(){
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public boolean isApproveFlag() {
        return approveFlag;
    }

    public void setApproveFlag(boolean approveFlag) {
        this.approveFlag = approveFlag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public User getExecutorUser() {
        return executorUser;
    }

    public void setExecutorUser(User executorUser) {
        this.executorUser = executorUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNeedApprove() {
        return needApprove;
    }

    public void setNeedApprove(boolean needApprove) {
        this.needApprove = needApprove;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public String getWfInstanceId() {
        return wfInstanceId;
    }

    public void setWfInstanceId(String wfInstanceId) {
        this.wfInstanceId = wfInstanceId;
    }
}
