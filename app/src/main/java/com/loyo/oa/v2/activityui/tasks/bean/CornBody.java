package com.loyo.oa.v2.activityui.tasks.bean;

import java.io.Serializable;

/**
 * Created by yyy on 16/3/29.
 */
public class CornBody implements Serializable {

    private int type = 0;
    private int day;
    private int weekDay;
    private int hour;
    private int minute;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }
}
