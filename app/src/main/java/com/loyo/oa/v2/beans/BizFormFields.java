package com.loyo.oa.v2.beans;

import java.io.Serializable;

public class BizFormFields implements Serializable {
    private String bizformId;
    private boolean canUpdate;
    private long createdAt;
    private String dbtype;
    private String defaultvalue;
    private boolean enable;
    private String id;
    private boolean isList;
    private String name;
    private long order;
    private String regularExpress;
    private boolean required;
    private long updatedAt;
    private String value;

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
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getDefaultvalue() {
        return defaultvalue == null ? "" : defaultvalue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
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
