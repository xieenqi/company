package com.loyo.oa.v2.activity.discuss;

/**
 * Created by xeq on 16/3/17.
 */
public class HttpMyDiscussItem {
    public String id;
    public String companyId;
    public String bizId;
    public String content;
    public String title;
//    public int bizType;//1.工作报告 2.任务 5.项目
    public String attachmentUUId;
    public Creator creator;
    //    public String atUsers;
    public boolean viewed;
    public boolean isAt;
    public String createdAt;
    public String updatedAt;

    public class Creator {
        public String id;
        public String name;
    }
}
