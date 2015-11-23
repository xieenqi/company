package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/23 0023.
 */
public class Product implements Serializable {
    private ArrayList<ExtraData> extDatas=new ArrayList<>();
    private String name ;
    private String prunit;
    private String unitPrice;
    private String uuid ;
    private String memo;//string, optional): ,
    private String id ;//int64, optional): ,

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPrunit() {
        return prunit;
    }

    public void setPrunit(String prunit) {
        this.prunit = prunit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ExtraData> getExtDatas() {
        return extDatas;
    }

    public void setExtDatas(ArrayList<ExtraData> extDatas) {
        this.extDatas = extDatas;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
