package com.loyo.oa.v2.activityui.customer.bean;


/**
 * Created by loyo_dev1 on 16/3/25.
 */
public class ContactRequestParam {

    public String val;
    public ContactLeftExtras properties = new ContactLeftExtras();
    public boolean required;//是否为必填项

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
