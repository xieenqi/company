package com.loyo.oa.v2.activityui.customer.model;

/**
 * 客户详情 参与人权限
 * Created by loyo_dev1 on 16/4/22.
 */
public class MembersRoot {

    private String id;
    private String Category;
    private String key;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
