package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by loyo_dev1 on 16/1/20.
 */
public class Modules implements Serializable{

    public String id;
    public String suiteId;
    public String name;
    public String code;
    public boolean enable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }




}
