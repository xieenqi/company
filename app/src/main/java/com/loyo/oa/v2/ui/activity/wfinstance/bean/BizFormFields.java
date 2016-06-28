package com.loyo.oa.v2.ui.activity.wfinstance.bean;

import java.io.Serializable;

public class BizFormFields implements Serializable {

    private long createdAt;
    private long order;
    private String bizformId;
    private String name;
    private String regularExpress;
    private String value;
    private String dbType;
    private String defaultValue;
    private String id;
    private boolean isList;
    private boolean required;
    private boolean enable;
    private boolean canUpdate;
    private boolean isSystem;
    private long updatedAt;


    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBizformId() {
        return bizformId;
    }

    public void setBizformId(String bizformId) {
        this.bizformId = bizformId;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDbtype() {
        return dbType;
    }

    public void setDbtype(String dbType) {
        this.dbType = dbType;
    }

    public String getDefaultvalue() {
        return defaultValue == null ? "" : defaultValue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultValue = defaultvalue;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean isList) {
        this.isList = isList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getRegularExpress() {
        return regularExpress;
    }

    public void setRegularExpress(String regularExpress) {
        this.regularExpress = regularExpress;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
