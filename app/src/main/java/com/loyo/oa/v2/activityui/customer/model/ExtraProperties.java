package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户动态字段
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class ExtraProperties implements Serializable {
    private String name;
    private String type;
    private String label;
    private String regExpress;
    private boolean required;
    private boolean enabled;
    private boolean isList;
    private ArrayList<String> defVal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String>  getDefVal() {
        return defVal;
    }

    public void setDefVal(ArrayList<String>  defVal) {
        this.defVal = defVal;
    }

    public boolean isList() {
        return isList;
    }

    public void setIsList(boolean isList) {
        this.isList = isList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRegExpress() {
        return regExpress;
    }

    public void setRegExpress(String regExpress) {
        this.regExpress = regExpress;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
