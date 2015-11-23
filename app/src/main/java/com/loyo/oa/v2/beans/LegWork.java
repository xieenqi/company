package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/12 0012.
 */
public class LegWork extends BaseBeans implements Serializable {
    private String address;//(string, optional): ,
    private ArrayList<Attachment> attachments;// (array[&{common Attachment}], optional): ,
    private long createdAt;// (&{time Time}, optional): ,
    private NewUser creator;//(&{organization User}, optional): ,
    private String customerId;//(int64, optional): ,
    private String customerName;//(string, optional): ,
    private String gpsInfo;//(string, optional): ,
    private String id;//(int64, optional): ,
    private String mentioned;//(string, optional): ,
    private String mentionedDeptIds;//(string, optional): ,
    private String mentionedGroupIds;// (string, optional): ,
    private String mentionedUserIds;// (string, optional): ,
    private long updatedAt;// (&{time Time}, optional):
    private String memo;// (&{time Time}, optional):
    private String attachmentUUId;
    private int totalLwCount;
    private int totalCustCount;
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getTotalCustCount() {
        return totalCustCount;
    }

    public void setTotalCustCount(int totalCustCount) {
        this.totalCustCount = totalCustCount;
    }

    public int getTotalLwCount() {
        return totalLwCount;
    }

    public void setTotalLwCount(int totalLwCount) {
        this.totalLwCount = totalLwCount;
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    String getOrderStr() {
        return getCreatedAt()+"";
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public NewUser getCreator() {
        return creator;
    }

    public void setCreator(NewUser creator) {
        this.creator = creator;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getGpsInfo() {
        return gpsInfo;
    }

    public void setGpsInfo(String gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMentioned() {
        return mentioned;
    }

    public void setMentioned(String mentioned) {
        this.mentioned = mentioned;
    }

    public String getMentionedDeptIds() {
        return mentionedDeptIds;
    }

    public void setMentionedDeptIds(String mentionedDeptIds) {
        this.mentionedDeptIds = mentionedDeptIds;
    }

    public String getMentionedGroupIds() {
        return mentionedGroupIds;
    }

    public void setMentionedGroupIds(String mentionedGroupIds) {
        this.mentionedGroupIds = mentionedGroupIds;
    }

    public String getMentionedUserIds() {
        return mentionedUserIds;
    }

    public void setMentionedUserIds(String mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
