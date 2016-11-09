package com.loyo.oa.voip.model;

import org.json.JSONObject;

/**
 * Created by EthanGong on 2016/11/8.
 */

public class RequestAccess {
    public String callLogId;
    String companyId;
    String userId;

    public String toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("callLogId", callLogId);
            json.put("companyId", companyId);
            json.put("userId", userId);
        }
        catch (Exception e) {}

        return json.toString();
    }
}
