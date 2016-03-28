package com.loyo.oa.v2.activity.customer;

import com.loyo.oa.v2.beans.NewTag;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/3/24.
 */
public class HttpAddCustomer implements Serializable {
    public ArrayList<NewTag> tags = new ArrayList<>();
    public String pname;
    public String ptel;
    public String wiretel;
    public String name;
    public String uuid;
    public int attachmentCount;
    public HttpLoc loc = new HttpLoc();

//    public class Loc {
//        public String addr;
//        public ArrayList<Long> loc = new ArrayList<>();
//    }
}
