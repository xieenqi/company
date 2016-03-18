package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Discussion implements Serializable, Comparable {

//    private int bizId;
//    private int bizType;
//    private String content;
//    private User creator;
//    private long createdAt;
//    private String attachmentUUId;// (string, optional): ,
//    private ArrayList<Attachment> attachments = new ArrayList<>();
//    private String id;
//    private String mentionedDeptIds;
//    private String mentionedGroupIds;
//    private String mentionedUserIds;
//
//    public String getMentionedUserIds() {
//        return mentionedUserIds;
//    }
//
//    public void setMentionedUserIds(String mentionedUserIds) {
//        this.mentionedUserIds = mentionedUserIds;
//    }
//
//    public String getMentionedGroupIds() {
//        return mentionedGroupIds;
//    }
//
//    public void setMentionedGroupIds(String mentionedGroupIds) {
//        this.mentionedGroupIds = mentionedGroupIds;
//    }
//
//    public String getMentionedDeptIds() {
//        return mentionedDeptIds;
//    }
//
//    public void setMentionedDeptIds(String mentionedDeptIds) {
//        this.mentionedDeptIds = mentionedDeptIds;
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
//    public ArrayList<Attachment> getAttachments() {
//        return attachments;
//    }
//
//    public void setAttachments(ArrayList<Attachment> attachments) {
//        this.attachments = attachments;
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
//    public User getCreator() {
//        return creator;
//    }
//
//    public void setCreator(User creator) {
//        this.creator = creator;
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
//    public int getBizId() {
//        return bizId;
//    }
//
//    public void setBizId(int bizId) {
//        this.bizId = bizId;
//    }
//
//    public int getBizType() {
//        return bizType;
//    }
//
//    public void setBizType(int bizType) {
//        this.bizType = bizType;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    @Override
//    public int compareTo(Object o) {
//        if (o == null || !(o instanceof Discussion)) {
//            return 1;
//        }
//
//        Discussion d = (Discussion) o;
//
//        Date thisDate = new Date(getCreatedAt() * 1000);
//        Date dDate = new Date(d.getCreatedAt() * 1000);
//
//        return thisDate.after(dDate) ? 1 : 0;
//    }

    /**
     * id : 56eb6327526f151c0800017d
     * attachmentUUId : a227601e-a77c-49e5-8615-e9cc5279e026
     * content : @侯超 zzz
     * creator : {"id":"56a47b15526f153bc1f38fa3","name":"王旭娇","gender":2,"depts":[{"shortDept":{"id":"56a47a8c526f153bc1f38fa1","xpath":"5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1","name":"测试1"},"shortPosition":{"id":"5698f5e07a101681d35da538","name":"普通员工","sequence":3},"title":"助理"}]}
     * summaryId : 56eb5d37526f151c08000007
     * createdAt : 1458266919
     * updatedAt : 1458266919
     * bizType : 1
     * mentionedUserIds : ["56d962f7526f150ed2000001"]
     */

    private String id;
    private String attachmentUUId;
    private String content;
    /**
     * id : 56a47b15526f153bc1f38fa3
     * name : 王旭娇
     * gender : 2
     * depts : [{"shortDept":{"id":"56a47a8c526f153bc1f38fa1","xpath":"5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1","name":"测试1"},"shortPosition":{"id":"5698f5e07a101681d35da538","name":"普通员工","sequence":3},"title":"助理"}]
     */

    private User creator;
    private String summaryId;
    private int createdAt;
    private int updatedAt;
    private int bizType;
    private List<String> mentionedUserIds;

    public void setId(String id) {
        this.id = id;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public void setMentionedUserIds(List<String> mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public String getId() {
        return id;
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public String getContent() {
        return content;
    }

    public User getCreator() {
        return creator;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public int getBizType() {
        return bizType;
    }

    public List<String> getMentionedUserIds() {
        return mentionedUserIds;
    }

    @Override
    public String toString() {
        return "HttpDiscussion{" +
                "id='" + id + '\'' +
                ", attachmentUUId='" + attachmentUUId + '\'' +
                ", content='" + content + '\'' +
                ", creator=" + creator +
                ", summaryId='" + summaryId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", bizType=" + bizType +
                ", mentionedUserIds=" + mentionedUserIds +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !(o instanceof Discussion)) {
            return 1;
        }

        Discussion d = (Discussion) o;

        Date thisDate = new Date(getCreatedAt() * 1000);
        Date dDate = new Date(d.getCreatedAt() * 1000);

        return thisDate.after(dDate) ? 1 : 0;
    }
}
