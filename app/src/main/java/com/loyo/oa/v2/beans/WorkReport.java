package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/27 0027.
 * <p/>
 * 工作报告
 */
public class WorkReport extends BaseBeans {
    public static final int DAY = 1, WEEK = 2, MONTH = 3;

    public ArrayList<WorkReportDyn> crmDatas;
    public boolean isDelayed;
    public ArrayList<Reviewer> reviewers;
    public Members members = new Members();//抄送人
    public NewUser user = new NewUser();
    public Reviewer reviewer = new Reviewer(user);//点评人
    public BizExtData bizExtData;


    public String attachmentUUId;//string, optional): ,
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public long beginAt;//&{time Time}, optional): ,
    public String content;//string, optional): ,
    public long createdAt;//&{time Time}, optional): ,
    public User creator;//&{organization User}, optional): ,
    public long endAt;//&{time Time}, optional): ,
    public String id;//int64, optional): ,
    public Project ProjectInfo;//int64, optional): ,
    public int type;//int, optional): ,
    public String title;//string, optional): ,
    public long updatedAt;//&{time Time}, optional):
    public DiscussCounter discuss;
    public boolean ack;
    @Override
    String getOrderStr() {
        return createdAt + "";
    }

    public String getId() {
        return id;
    }
//    public BizExtData getBizExtData() {
//        return bizExtData;
//    }
//
//    public void setBizExtData(BizExtData bizExtData) {
//        this.bizExtData = bizExtData;
//    }
//
//    public NewUser getUser() {
//        return user;
//    }
//
//    public void setUser(NewUser user) {
//        this.user = user;
//    }
//
//    public void setReviewer(Reviewer reviewer) {
//        this.reviewer = reviewer;
//    }
//
//    public void setMembers(Members members) {
//        this.members = members;
//    }
//
//    public Members getMembers() {
//        return members;
//    }
//
//
//    public ArrayList<Reviewer> getReviewers() {
//        return reviewers;
//    }
//
//    public boolean isDelayed() {
//        return isDelayed;
//    }
//
//    public void setEndAt(long endAt) {
//        this.endAt = endAt;
//    }
//
//
//    public ArrayList<WorkReportDyn> getCrmDatas() {
//        return crmDatas;
//    }
//
//    public DiscussCounter getDiscuss() {
//        return discuss;
//    }
//
//    public void setAck(boolean ack) {
//        this.ack = ack;
//    }
//
//
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
//    public long getBeginAt() {
//        return beginAt;
//    }
//
//    public String getContent() {
//        return content == null ? "" : content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
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
//    public long getEndAt() {
//        return endAt;
//    }
//

//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public Project getProject() {
//        return ProjectInfo;
//    }
//
//    public void setProject(Project project) {
//        this.ProjectInfo = project;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
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
//    public long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(long updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public Reviewer getReviewer() {
//        return reviewer;
//    }

    @Override
    public String getOrderStr2() {
        return null != reviewer && reviewer.isReviewed() ? "1" : "0";
    }

    /**
     * 获取点评人
     *
     * @return
     */
    public boolean isReviewed() {
        return null != reviewer && reviewer.isReviewed() ? true : false;
    }
}
