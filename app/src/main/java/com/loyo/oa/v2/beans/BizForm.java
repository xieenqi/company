package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/29 0029.
 */
public class BizForm implements Serializable {

    private long createdAt;//&{time Time}, optional): ,
    private boolean enable;//bool, optional): ,
    private ArrayList<BizFormFields> fields;//array[BizFormFields], optional): ,
    private String id;//int64, optional): ,
    private String name;//string, optional): ,
    private long updatedAt;//&{time Time}, optional):
    public int bizCode;


    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ArrayList<BizFormFields> getFields() {
        return fields;
    }

    public void setFields(ArrayList<BizFormFields> fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
