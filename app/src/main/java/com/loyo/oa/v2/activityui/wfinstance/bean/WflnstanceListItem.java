package com.loyo.oa.v2.activityui.wfinstance.bean;

import java.io.Serializable;

/**
 * Created by xeq on 16/7/21.
 * 我提交的 我审批的  列表 item 数据模板
 */
public class WflnstanceListItem implements Serializable {

    public int approveStatus;
    public String bizformName;
    public long createdAt;
    public String id;
    public String nextExecutorName;
    public int status;
    public String title;
    public boolean viewed;
//    {
//        approveStatus = 1;
//        bizformName = item1;
//        createdAt = 1469086977;
//        id = 57907d01526f152e476e3445;
//        nextExecutorName = "魏诗语";
//        status = 1;
//        title = "孙文提交item1";
//        viewed = 0;
//    },
}
