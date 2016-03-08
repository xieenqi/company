package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/22 0022.
 */
public class SaleActivity extends BaseBeans implements Serializable {
    private ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    private String content;//string, optional): ,
    private long createAt;//&{time Time}, optional): ,
    private NewUser creator;//&{organization User}, optional): ,
    private String customerId;//int64, optional): ,
    private String id;//int, optional): ,
    private String opportunityId;//int64, optional): ,
    private String tagItemIds;//string, optional): ,
    private CommonTag type;//array[&{tag TagItem}], optional): ,
    private long updatedAt;//&{time Time}, optional):
    private long remindAt;

    @Override
    String getOrderStr() {
        return getCreateAt()+"";
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public long getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(long remindAt) {
        this.remindAt = remindAt;
    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getTagItemIds() {
        return tagItemIds;
    }

    public void setTagItemIds(String tagItemIds) {
        this.tagItemIds = tagItemIds;
    }

    public CommonTag getType() {
        return type;
    }

    public void setType(CommonTag type) {
        this.type = type;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
