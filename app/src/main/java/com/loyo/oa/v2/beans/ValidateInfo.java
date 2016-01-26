package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :考勤打卡信息
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class ValidateInfo implements Serializable {

    private long serverTime;
    private TimeSetting setting;// (TimeSetting, optional): ,
    private ArrayList<ValidateItem> valids = new ArrayList<>();//array[ValidateItem], optional):

    public ArrayList<ValidateItem> getValids() {
        return valids;
    }

    public void setValids(ArrayList<ValidateItem> valids) {
        this.valids = valids;
    }

    public TimeSetting getSetting() {
        return setting;
    }

    public void setSetting(TimeSetting setting) {
        this.setting = setting;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }
}
