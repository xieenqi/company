package com.loyo.oa.v2.customview.classify_seletor;

/**
 * 产品选择器的数据模型
 * Created by jie on 16/12/30.
 */

public class ClassifySeletorItem {
    public String id;
    public String name;
    public String path;
    public Boolean isRoot;
    public Integer level;
    public String parentId;
    public Boolean isFinal;//是不是最后一级，为null，表示没有计算，为false为不是，为true是最后一级


    public ClassifySeletorItem(){}

    public ClassifySeletorItem(String id, String name, String path, Boolean isRoot, Integer level, String parentId) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isRoot = isRoot;
        this.level = level;
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getRoot() {
        return isRoot;
    }

    public void setRoot(Boolean root) {
        isRoot = root;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getFinal() {
        return isFinal;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }
}
