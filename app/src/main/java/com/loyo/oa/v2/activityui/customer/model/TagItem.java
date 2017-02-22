package com.loyo.oa.v2.activityui.customer.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/17 0017.
 */
public class TagItem  implements Serializable,Comparable<TagItem>{
    private String id	;//	2

    private String name;//	跟进客户

    private boolean isChecked = false;

    private String tagId;

    private String order;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }


    public String getTagId()
    {
        return tagId;
    }

    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }

    public boolean isChecked() {return isChecked;}

    public void setIsChecked(boolean isChecked) {this.isChecked = isChecked;}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name ==null?"": name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(TagItem tagItem)
    {
        int result=-1;

        if(TextUtils.equals(tagId,tagItem.getTagId()))
            result=0;
//        else if(tagId>tagItem.getTagId())
//            result= 1;

        return result;
    }

    @Override
    public String toString() {
        return "["+"id="+id+"\nname="+name+"\nisChecked="+isChecked+"\ntagId"+tagId+"\norder="+order+"]";
    }
}
