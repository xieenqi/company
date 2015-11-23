package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/22 0022.
 */
public class Tag implements Serializable {

    private boolean enable ;
    private String id ;
    private ArrayList<TagItem>items=new ArrayList<>();
    private String name ;
    private String type ;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ArrayList<TagItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TagItem> items) {
        this.items = items;
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
}
