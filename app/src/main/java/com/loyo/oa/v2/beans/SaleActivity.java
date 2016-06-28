package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.ui.activity.attachment.bean.Attachment;
import com.loyo.oa.v2.ui.activity.sale.bean.CommonTag;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/22 0022.
 */
public class SaleActivity extends BaseBeans implements Serializable {
    private ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    private NewUser creator;//&{organization User}, optional): ,
    private String customerId;//int64, optional): ,
    private String opportunityId;//int64, optional): ,
    private String tagItemIds;//string, optional): ,
    private CommonTag type;//array[&{tag TagItem}], optional): ,
    private long updatedAt;//&{time Time}, optional):

    //精简过后的跟进动态 ben  20160612
    public String id;
    public String creatorName;
    public long createAt;
    public String content;
    public String typeName;
    public long remindAt;
    public String contactName;

    //    type SaleActivitySimple struct {
//        Id          bson.ObjectId `json:"id"`
//        CreatorName string        `json:"creatorName"` //跟进人
//        CreateAt    int64         `json:"createAt"`
//        Content     string        `json:"content,omitempty"`
//        RemindAt    int64         `json:"remindAt"`
//        TypeName    string        `json:"typeName,omitempty"`
//        ContactId   string        `json:"-"`
//        ContactName string        `json:"contactName,omitempty"` //联系人
//    }
    @Override
    String getOrderStr() {
        return getCreateAt() + "";
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
