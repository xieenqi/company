package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/3/24.
 */
public class ContactExtras implements Serializable {

    public String name;
    public String fieldName;
    public String type;
    public String label;
    public boolean required;
    public boolean enabled;
    public boolean sortable;
    public boolean isList;
    public boolean isSystem;
    public boolean isCustom;
    public boolean canEdit;
    public String val;
    public ArrayList<String> defVal = new ArrayList<>();


//    public ArrayList<String> getDefVal() {
//        return defVal;
//    }
//
//    public void setDefVal(ArrayList<String> defVal) {
//        this.defVal = defVal;
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
//    public String getVal() {
//        return val;
//    }
//
//    public void setVal(String val) {
//        this.val = val;
//    }
//
//    public boolean isCanEdit() {
//        return canEdit;
//    }
//
//    public void setCanEdit(boolean canEdit) {
//        this.canEdit = canEdit;
//    }
//
//    public boolean isCustom() {
//        return isCustom;
//    }
//
//    public void setIsCustom(boolean isCustom) {
//        this.isCustom = isCustom;
//    }
//
//    public boolean isSystem() {
//        return isSystem;
//    }
//
//    public void setIsSystem(boolean isSystem) {
//        this.isSystem = isSystem;
//    }
//
//    public boolean isList() {
//        return isList;
//    }
//
//    public void setIsList(boolean isList) {
//        this.isList = isList;
//    }
//
//    public boolean isSortable() {
//        return sortable;
//    }
//
//    public void setSortable(boolean sortable) {
//        this.sortable = sortable;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public boolean isRequired() {
//        return required;
//    }
//
//    public void setRequired(boolean required) {
//        this.required = required;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//
//    public void setLabel(String label) {
//        this.label = label;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getFieldName() {
//        return fieldName;
//    }
//
//    public void setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//    }
}
