package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/27 0027.
 */
public class WorkReport extends BaseBeans {
    public static final int DAY = 1, WEEK = 2, MONTH = 3;

    private ArrayList<CRMData> crmDatas;
    private boolean isDelayed ;
    private ArrayList<Member> members;
    private ArrayList<Reviewer> reviewers;

    public ArrayList<Reviewer> getReviewers() {
        return reviewers;
    }

    public void setReviewers(ArrayList<Reviewer> reviewers) {
        this.reviewers = reviewers;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public boolean isDelayed() {
        return isDelayed;
    }

    public void setIsDelayed(boolean isDelayed) {
        this.isDelayed = isDelayed;
    }

    public void setEndAt(long endAt) {
        this.endAt = endAt;
    }

    public ArrayList<CRMData> getCrmDatas() {
        return crmDatas;
    }

    public void setCrmDatas(ArrayList<CRMData> crmDatas) {
        this.crmDatas = crmDatas;
    }

    private String attachmentUUId;//string, optional): ,
    private ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    private long beginAt;//&{time Time}, optional): ,
    private String content;//string, optional): ,
    private long createdAt;//&{time Time}, optional): ,
    private User creator;//&{organization User}, optional): ,
    private long endAt;//&{time Time}, optional): ,
    private String id;//int64, optional): ,
    private Project ProjectInfo;//int64, optional): ,
    private int type;//int, optional): ,
    private String title;//string, optional): ,
    private long updatedAt;//&{time Time}, optional):
    private DiscussCounter discuss;

    public DiscussCounter getDiscuss() {
        return discuss;
    }

    public void setDiscuss(DiscussCounter discuss) {
        this.discuss = discuss;
    }

    @Override
    String getOrderStr() {
        return getCreatedAt()+"";
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    private boolean ack;

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public long getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(long beginAt) {
        this.beginAt = beginAt;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public long getEndAt() {
        return endAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Project getProject() {
        return ProjectInfo;
    }

    public void setProject(Project project) {
        this.ProjectInfo = project;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Reviewer getReviewer() {
        return null==getReviewers()||getReviewers().isEmpty()?null:getReviewers().get(0);
    }

    @Override
    public String getOrderStr2() {
        return null!=getReviewer()&&getReviewer().isReviewed()?"1":"0";
    }

    /**
     * 获取点评人
     * @return
     */
    public boolean isReviewed(){
        return null!=getReviewer()&&getReviewer().isReviewed()?true:false;
    }

    /**
     * 获取参与人
     * @return
     */
    public String getJoinUserNames(){
        if(null==members||members.isEmpty()){
            return "";
        }
        StringBuilder result=new StringBuilder();
        for (int i = 0; i <members.size() ; i++) {
            if(null!=members.get(i).getUser()) {
                result.append(members.get(i).getUser().getName());
            }
            if(i<members.size()-1){
                result.append(",");
            }
        }

        return result.toString();
    }

}
