package com.loyo.oa.v2.activityui.customer.model;

import java.io.Serializable;

/**
 * Created by yyy on 16/10/27.
 */

public class MyContactInfo implements Serializable{

    private String name;
    private String phono;
    private String sortLetters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhono() {
        return phono;
    }

    public void setPhono(String phono) {
        this.phono = phono;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
