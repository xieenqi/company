package com.loyo.oa.v2.activity.discuss;

/**
 * Created by xeq on 16/3/17.
 */
public class HttpDiscussItem {
    public String id;
    public String companyId;
    public String summaryId;
    public String bizId;
    public String content;
    public String title;
    public int bizType;
    public String attachmentUUId;
    public Creator creator;
    //    public String atUsers;
    public boolean viewed;
    public String createdAt;
    public String updatedAt;

    public class Creator {
        public String id;
        public String name;
    }
}
