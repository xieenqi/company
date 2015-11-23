package com.loyo.oa.v2.beans.Parameters;

import com.loyo.oa.v2.tool.json.JSONObjectUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/29 0029.
 */
public class WorkFlowValuesAdd implements Serializable {
    private String   wfInstanceId ;//int64, optional): ,
    private ArrayList<JSONObjectUtil> wfInstanceValuesDatas ;//array[&{bson M}], optional):
    public WorkFlowValuesAdd(){
        wfInstanceValuesDatas=new ArrayList<JSONObjectUtil>();

    }
    public String getWfInstanceId() {
        return wfInstanceId;
    }

    public void setWfInstanceId(String wfInstanceId) {
        this.wfInstanceId = wfInstanceId;
    }

    public ArrayList<JSONObjectUtil> getWfInstanceValuesDatas() {
        return wfInstanceValuesDatas;
    }

    public void setWfInstanceValuesDatas(ArrayList<JSONObjectUtil> wfInstanceValuesDatas) {
        this.wfInstanceValuesDatas = wfInstanceValuesDatas;
    }
}
