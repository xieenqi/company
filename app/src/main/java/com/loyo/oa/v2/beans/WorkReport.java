package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/27 0027.
 * <p/>
 * 工作报告
 */
public class WorkReport extends BaseBeans {
    public static final int DAY = 1, WEEK = 2, MONTH = 3;

    public ArrayList<WorkReportDyn> crmDatas;
    public boolean isDelayed;
    public ArrayList<Reviewer> reviewers;
    public Members members = new Members();//抄送人
    public NewUser user = new NewUser();
    public Reviewer reviewer = new Reviewer(user);//点评人
    public BizExtData bizExtData;


    public String attachmentUUId;//string, optional): ,
    public ArrayList<Attachment> attachments;//array[&{common Attachment}], optional): ,
    public long beginAt;//&{time Time}, optional): ,
    public String content;//string, optional): ,
    public long createdAt;//&{time Time}, optional): ,
    public User creator;//&{organization User}, optional): ,
    public long endAt;//&{time Time}, optional): ,
    public String id;//int64, optional): ,
    public Project ProjectInfo;//int64, optional): ,
    public int type;//int, optional): ,
    public String title;//string, optional): ,
    public long updatedAt;//&{time Time}, optional):
    public DiscussCounter discuss;
    public boolean ack;

    @Override
    String getOrderStr() {
        return createdAt + "";
    }

    public String getId() {
        return id;
    }

    @Override
    public String getOrderStr2() {
        return null != reviewer && reviewer.isReviewed() ? "1" : "0";
    }

    /**
     * 点评人是否点评过
     *
     * @return
     */
    public boolean isReviewed() {
        return null != reviewer && reviewer.isReviewed() ? true : false;
    }
}
