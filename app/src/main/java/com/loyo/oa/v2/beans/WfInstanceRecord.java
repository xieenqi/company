package com.loyo.oa.v2.beans;
import java.io.Serializable;

/**
 * 【审批列表】精简接口bean
 * Created by yyy on 16/6/21.
 */
public class WfInstanceRecord extends BaseBeans implements Serializable{

    public String bizformName;
    public long createdAt;
    public String id;
    public String nextExecutorName;
    public int status;
    public String title;
    public boolean viewed;

    @Override
    String getOrderStr() {
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
