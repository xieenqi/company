package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :打卡列表
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class AttendanceList implements Serializable {
    private ArrayList<DayofAttendance> attendances = new ArrayList<>();// (array[DayOfAttendance], optional): ,
    private int earlyCount;// (int, optional): ,
    private int lateCount;// (int, optional): ,
    private int listType;// (int, optional): ,
    private int noreCcount;// (int, optional): ,
    private int outsidecount;// (int, optional):

    public int getEarlyCount() {
        return earlyCount;
    }

    public void setEarlyCount(int earlyCount) {
        this.earlyCount = earlyCount;
    }

    public void setOutsidecount(int outsidecount) {
        this.outsidecount = outsidecount;
    }

    public int getNoreCcount() {
        return noreCcount;
    }

    public void setNoreCcount(int noreCcount) {
        this.noreCcount = noreCcount;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public int getLateCount() {
        return lateCount;
    }

    public void setLateCount(int lateCount) {
        this.lateCount = lateCount;
    }

    public int getOutsidecount() {
        return outsidecount;
    }

    public ArrayList<DayofAttendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(ArrayList<DayofAttendance> attendances) {
        this.attendances = attendances;
    }

}
