package com.loyo.oa.v2.beans;


/**
 * Created by loyo_dev1 on 16/3/25.
 */
public class ContactRequestParam {

    public String val;
    public ContactExtras properties = new ContactExtras();

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}