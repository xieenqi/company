package com.loyo.oa.v2.activityui.wfinstance.bean;

import com.loyo.oa.v2.activityui.wfinstance.common.SubmitStatus;
import com.loyo.oa.v2.common.GroupKey;
import com.loyo.oa.v2.common.Groupable;

import java.io.Serializable;

/**
 * Created by EthanGong on 2017/1/9.
 */

public class ApprovalItemModel implements Groupable<ApprovalItemModel>, Serializable {
    public int approveStatus; //  TODO:
    public String bizformName;
    public long createdAt;
    public String id;
    public String nextExecutorName;
    public SubmitStatus status;
    public String title;
    public boolean viewed;

    @Override
    public GroupKey groupBy() {
        return status;
    }

    @Override
    public int compareTo(ApprovalItemModel another) {
        if (createdAt < another.createdAt) {
            return 1;
        }
        else if (createdAt > another.createdAt) {
            return -1;
        }
        return 0;
    }
}
