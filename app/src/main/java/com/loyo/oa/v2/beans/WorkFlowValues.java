package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Administrator on 2014/12/29 0029.
 */
public class WorkFlowValues implements Serializable {
    private String wfInstanceId ;//int64, optional): ,
    private HashMap<String,String>values=new HashMap<>();


    public HashMap<String, String> getValues() {
        return values;
    }

    public void setValues(HashMap<String, String> values) {
        this.values = values;
    }

    public String getWfInstanceId() {
        return wfInstanceId;
    }

    public void setWfInstanceId(String wfInstanceId) {
        this.wfInstanceId = wfInstanceId;
    }
}
