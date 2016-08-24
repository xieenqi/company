package com.loyo.oa.v2.beans;
import java.io.Serializable;

/**
 * 【报告列表】精简接口bean
 * Created by yyy on 16/6/28.
 */
public class WorkReportRecord extends BaseBeans implements Serializable{

    public long createdAt;
    public String id;
    public boolean isDelayed;
    public int status;
    public String reviewerName;
    public String title;
    public int type;
    public boolean viewed;
    public String attachmentUUId;


    @Override
    public String getOrderStr() {
        return status + "";
    }

    @Override
    public String getOrderStr2() {
        return status + "";
    }

    @Override
    public String getId() {
        return id;
    }
}
