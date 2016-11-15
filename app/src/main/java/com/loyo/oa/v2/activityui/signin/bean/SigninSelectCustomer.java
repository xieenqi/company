package com.loyo.oa.v2.activityui.signin.bean;

import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.beans.Location;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomer implements Serializable {
    public String id;
    public String name;
    public Location loc;
    public ArrayList<Contact> contacts = new ArrayList<>();
    public double distance;
}
