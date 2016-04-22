package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/23 0023.
 */
public class Product implements Serializable {
    public ArrayList<ExtraData> extDatas = new ArrayList<>();
    public String name;
    public String prunit;
    public String unitPrice;
    public String unit;
    public String memo;//string, optional): ,
    public String id;//int64, optional): ,

//    public String getUnitPrice() {
//        return unitPrice;
//    }
//
//    public void setUnitPrice(String unitPrice) {
//        this.unitPrice = unitPrice;
//    }
//
//    public String getUnid() {
//        return unit;
//    }
//
//    public void setUnid(String uuid) {
//        this.unit = uuid;
//    }
//
//    public String getPrunit() {
//        return prunit;
//    }
//
//    public void setPrunit(String prunit) {
//        this.prunit = prunit;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public ArrayList<ExtraData> getExtDatas() {
//        return extDatas;
//    }
//
//    public void setExtDatas(ArrayList<ExtraData> extDatas) {
//        this.extDatas = extDatas;
//    }
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
}
