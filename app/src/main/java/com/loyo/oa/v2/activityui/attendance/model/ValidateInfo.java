package com.loyo.oa.v2.activityui.attendance.model;

import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.activityui.attendance.model.TimeSetting;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :考勤打卡信息
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class ValidateInfo implements Serializable {


    private int extraMins;
    private long extraStartTime;
    private long serverTime;
    private boolean needExtra;
    private boolean needPhoto;
    private boolean isPopup;
    private boolean isWorkDay;
    private boolean extraTimeSwitch;

    public boolean isExtraTimeSwitch() {
        return extraTimeSwitch;
    }

    public void setExtraTimeSwitch(boolean extraTimeSwitch) {
        this.extraTimeSwitch = extraTimeSwitch;
    }

    public boolean isWorkDay() {
        return isWorkDay;
    }

    public void setIsWorkDay(boolean isWorkDay) {
        this.isWorkDay = isWorkDay;
    }

    public boolean isNeedExtra() {
        return needExtra;
    }

    public void setNeedExtra(boolean needExtra) {
        this.needExtra = needExtra;
    }

    private ArrayList<TimeSetting> setting = new ArrayList<>();// (TimeSetting, optional): ,
    public ArrayList<ValidateItem> valids = new ArrayList<>();//array[ValidateItem], optional):

    public ArrayList<TimeSetting> getSetting() {
        return setting;
    }

    public void setSetting(ArrayList<TimeSetting> setting) {
        this.setting = setting;
    }

    public ArrayList<ValidateItem> getValids() {
        return valids;
    }

    public void setValids(ArrayList<ValidateItem> valids) {
        this.valids = valids;
    }

    public long getServerTime() {
        return serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public boolean isNeedPhoto() {
        return needPhoto;
    }

    public void setNeedPhoto(boolean needPhoto) {
        this.needPhoto = needPhoto;
    }

    public long getExtraStartTime() {
        return extraStartTime;
    }

    public void setExtraStartTime(long extraStartTime) {
        this.extraStartTime = extraStartTime;
    }

    public boolean isPopup() {
        return isPopup;
    }

    public void setIsPopup(boolean isPopup) {
        this.isPopup = isPopup;
    }

    public int getExtraMins() {
        return extraMins;
    }

    public void setExtraMins(int extraMins) {
        this.extraMins = extraMins;
    }
}
