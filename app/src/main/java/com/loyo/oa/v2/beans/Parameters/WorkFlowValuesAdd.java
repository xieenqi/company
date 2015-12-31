package com.loyo.oa.v2.beans.Parameters;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/29 0029.
 */
public class WorkFlowValuesAdd implements Serializable {
    public String   wfInstanceId ;//int64, optional): ,
    public ArrayList<JSONObject> wfInstanceValuesDatas ;//array[&{bson M}], optional):



    public WorkFlowValuesAdd(){
        wfInstanceValuesDatas=new ArrayList<JSONObject>();

    }
//    public String getWfInstanceId() {
//        return wfInstanceId;
//    }
//
//    public void setWfInstanceId(String wfInstanceId) {
//        this.wfInstanceId = wfInstanceId;
//    }
//
//    public ArrayList<JSONObjectUtil> getWfInstanceValuesDatas() {
//        return wfInstanceValuesDatas;
//    }
//
//    public void setWfInstanceValuesDatas(ArrayList<JSONObjectUtil> wfInstanceValuesDatas) {
//        this.wfInstanceValuesDatas = wfInstanceValuesDatas;
//    }
}
