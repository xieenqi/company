package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.other.bean.User;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :子任务封装类
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
public class TaskCheckPoint extends BaseBeans implements Serializable{
    public static final int STATUS_PROCESSING = 1;  //未完成
    public static final int STATUS_REVIEWING = 2;   //审核中
    public static final int STATUS_FINISHED = 3;    //已完成

    boolean achieved;
    String content;
    String createdAt;
    User creator;
    String deadline;
    String id;
    NewUser responsiblePerson;
    long sequenceNumber;
    String taskId;
    String title;
    String updatedAt;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;


    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getcontent() {
        return content;
    }

    public void setcontent(String content) {
        this.content = content;
    }

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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public NewUser getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(NewUser responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    String getOrderStr() {
        return null;
    }
}
