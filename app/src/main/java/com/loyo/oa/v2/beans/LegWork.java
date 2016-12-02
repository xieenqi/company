package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.customer.model.Customer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *xnq
 */
public class LegWork extends BaseBeans implements Serializable {

    public String gpsInfo;
    public String position;
    public String address;//(string, optional): ,
    public ArrayList<Attachment> attachments;// (array[&{common Attachment}], optional): ,
    public long createdAt;// (&{time Time}, optional): ,
    public NewUser creator=new NewUser();//(&{organization User}, optional): ,
    public String customerId;//(int64, optional): ,
    public String customerName;//(string, optional): ,
    public String id;//(int64, optional): ,
    public long updatedAt;// (&{time Time}, optional):
    public String memo;// (&{time Time}, optional):
    public String attachmentUUId;
    public Customer customer;

    @Override
    public String getOrderStr() {
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
