package com.loyo.oa.v2.activityui.discuss;


import com.loyo.oa.v2.activityui.discuss.bean.HttpCrecter;

/**
 * Created by xeq on 16/3/17.
 */
public class HttpMyDiscussItem {
    public String id;
    public String companyId;
    public String bizId;
    public String atContent;

    public String title;
    public int bizType;//1.工作报告 2.任务 5.项目
    public String attachmentUUId;
    public HttpCrecter creator;
    public boolean viewed;
    public boolean isAt;
    public String createdAt;
    public String updatedAt;
    public long newUpdatedAt;
}
