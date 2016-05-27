package com.loyo.oa.v2.activity.sale.bean;

import com.loyo.oa.v2.beans.ContactLeftExtras;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 机会详情bean
 * Created by yyy on 16/5/20.
 */
public class SaleDetails implements Serializable {
    public String id;
    public String creatorId;
    public String name;          //标题
    public String cusName;       //对应客户
    public String customerId;
    public int salesAmount;      //预估销售金额
    public float estimatedAmount;  //预计成交时间
    public String stageName;     //阶段名
    public String stageId;
    public int estimatedTime;
    public int prob;             //阶段百分比
    public ArrayList<SaleIntentionalProduct> proInfos = new ArrayList<>();
    public String lostReason;    //输单原因
    public String chanceType;    //机会类型
    public String chanceSource;  //机会来源
    public String memo;          //备注
    public String creatorName;   //创建人
    public int createdAt;        //创建时间
    public int updatedAt;        //更新时间
    public int winTime;          //赢单时间
    public ArrayList<ContactLeftExtras> extensionDatas = new ArrayList<>();    //动态字段


    public ArrayList<SaleIntentionalProduct> getProInfos() {
        return proInfos;
    }

    public void setProInfos(ArrayList<SaleIntentionalProduct> proInfos) {
        this.proInfos = proInfos;
    }

    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public int getWinTime() {
        return winTime;
    }

    public void setWinTime(int winTime) {
        this.winTime = winTime;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getChanceSource() {
        return chanceSource;
    }

    public void setChanceSource(String chanceSource) {
        this.chanceSource = chanceSource;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String customerName) {
        this.cusName = customerName;
    }

    public int getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(int salesAmount) {
        this.salesAmount = salesAmount;
    }

    public float getEstimatedAmount() {
        return estimatedAmount;
    }


    public String getChanceType() {
        return chanceType;
    }

    public void setChanceType(String chanceType) {
        this.chanceType = chanceType;
    }


    public class SaleDetailsExtraList {
        private String val;
        private SaleDetailsExtra properties;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public SaleDetailsExtra getProperties() {
            return properties;
        }

        public void setProperties(SaleDetailsExtra properties) {
            this.properties = properties;
        }
    }
}
