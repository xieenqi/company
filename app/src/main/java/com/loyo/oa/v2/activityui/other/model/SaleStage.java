package com.loyo.oa.v2.activityui.other.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/12/23 0023.
 */
public class SaleStage implements Serializable {

    private String id ;//int64, optional): ,
    private String name ;//string, optional): ,
    private float prob;//float32, optional): ,


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProb() {
        return prob;
    }

    public void setProb(float prob) {
        this.prob = prob;
    }
}
