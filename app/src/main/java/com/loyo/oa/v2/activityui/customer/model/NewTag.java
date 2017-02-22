package com.loyo.oa.v2.activityui.customer.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版客户标签
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class NewTag implements Serializable ,Comparable<NewTag> {

    public String tId;
    public String itemId;
    public String itemName;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }
    @Override
    public int compareTo(NewTag tag)
    {
        return tId.compareTo(tag.gettId());
    }

}
