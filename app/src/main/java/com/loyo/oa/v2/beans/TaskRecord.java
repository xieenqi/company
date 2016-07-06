package com.loyo.oa.v2.beans;
import com.loyo.oa.v2.activityui.tasks.bean.CornBody;

import java.io.Serializable;

/**
 * 【任务列表】精简接口bean
 * Created by yyy on 16/6/22.
 */
public class TaskRecord extends BaseBeans implements Serializable{

    public String id;
    public long planendAt;
    public long actualendAt;
    public String responsibleName;
    public int status;
    public String title;
    public boolean viewed;
    public CornBody cornBody;

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
