package com.loyo.oa.v2.activity.discuss;

import java.util.ArrayList;

/**
 * 讨论详情 的模板
 * Created by loyocloud on 16/3/10.
 */
public class HttpDiscussDet {
    public String id;
    public String attachmentUUId;
    public String content;
    public String summaryId;
    public long createdAt;
    public long updatedAt;
    public ArrayList<String> mentionedUserIds;
    public HttpCrecter creator;

}