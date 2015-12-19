package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *xnq
 */
public class LegWork extends BaseBeans implements Serializable {
    public String address;//(string, optional): ,
    public ArrayList<Attachment> attachments;// (array[&{common Attachment}], optional): ,
    public long createdAt;// (&{time Time}, optional): ,
    public NewUser creator;//(&{organization User}, optional): ,
    public String customerId;//(int64, optional): ,
    public String customerName;//(string, optional): ,
//    public String gpsInfo;//(string, optional): ,
    public String id;//(int64, optional): ,
//    public String mentioned;//(string, optional): ,
//    public String mentionedDeptIds;//(string, optional): ,
//    public String mentionedGroupIds;// (string, optional): ,
//    public String mentionedUserIds;// (string, optional): ,
    public long updatedAt;// (&{time Time}, optional):
    public String memo;// (&{time Time}, optional):
    public String attachmentUUId;
//    public int totalLwCount;
//    public int totalCustCount;
    public Customer customer;

    @Override
    String getOrderStr() {
        return getCreatedAt() + "";
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

}
