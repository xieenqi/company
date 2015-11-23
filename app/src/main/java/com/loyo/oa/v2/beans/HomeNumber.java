package com.loyo.oa.v2.beans;

/**
 * Created by pj on 15/4/13.
 */
public class HomeNumber {

    public int getBiz_type() {
        return biz_type;
    }

    public void setBiz_type(int biz_type) {
        this.biz_type = biz_type;
    }

    public String getBiz_name() {
        return biz_name;
    }

    public void setBiz_name(String biz_name) {
        this.biz_name = biz_name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    private int biz_type;
    private String biz_name;
    private int number;


}
