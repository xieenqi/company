package com.loyo.oa.v2.beans;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.bean.Product;
import com.loyo.oa.v2.activityui.other.bean.SaleStage;
import com.loyo.oa.v2.activityui.other.bean.User;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/23 0023.
 */
public class Demand extends BaseBeans {


    private ArrayList<CommonTag> loseReason = new ArrayList<>();
    private String wfId; //赢单审批 id
    private int wfState;//赢单审批状态 1.待审核 2.审核中 3.未通过 4.审批通过 5.审批完结  0.非赢单状态
    private float amount;//float32, optional): ,
    private ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    private long createdAt;//&{time Time}, optional): ,
    private User creator;//&{organization User}, optional): ,
    private String customerId;//&{customer Customer}, optional): ,
    private User editor;//&{organization User}, optional): ,
    private String id;//int64, optional): ,
    private double estimatedNum;//float32, optional): ,
    private double estimatedPrice;//float32, optional): ,

    private Product product;//&{product Product}, optional): 产品,
    private SaleStage saleStage;//&{setting SaleStage}, optional): ,
    private User seller;//&{organization User}, optional): ,
    private String spec;//string, optional): ,
    private String updatedAt;//&{time Time}, optional):
    private String memo;
    private double actualPrice;
    private double actualNum;


    public int getWfState() {
        return wfState;
    }

    public void setWfState(int wfState) {
        this.wfState = wfState;
    }

    public String getWfId() {
        return wfId;
    }

    public void setWfId(String wfId) {
        this.wfId = wfId;
    }

    public ArrayList<CommonTag> getLoseReason() {
        return loseReason;
    }

    public void setLoseReason(ArrayList<CommonTag> loseReason) {
        this.loseReason = loseReason;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMemo() {
        return TextUtils.isEmpty(memo) ? "" : memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public double getActualNum() {
        return actualNum;
    }

    public void setActualNum(float actualNum) {
        this.actualNum = actualNum;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(float actualPrice) {
        this.actualPrice = actualPrice;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }


    public User getEditor() {
        return editor;
    }

    public void setEditor(User editor) {
        this.editor = editor;
    }

    @Override
    public String getOrderStr() {
        return "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getEstimatedNum() {
        return estimatedNum;
    }

    public void setEstimatedNum(float estimatedNum) {
        this.estimatedNum = estimatedNum;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(float estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public SaleStage getSaleStage() {
        return saleStage;
    }

    public void setSaleStage(SaleStage saleStage) {
        this.saleStage = saleStage;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
