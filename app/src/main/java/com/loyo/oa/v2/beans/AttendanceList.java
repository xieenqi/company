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
    private int earlycount;// (int, optional): ,
    private int latecount;// (int, optional): ,
    private int listtype;// (int, optional): ,
    private int noreccount;// (int, optional): ,
    private int outsidecount;// (int, optional):

    public int getOutsidecount() {
        return outsidecount;
    }

    public void setOutsidecount(int outsidecount) {
        this.outsidecount = outsidecount;
    }

    public int getNoreccount() {
        return noreccount;
    }

    public void setNoreccount(int noreccount) {
        this.noreccount = noreccount;
    }

    public int getListtype() {
        return listtype;
    }

    public void setListtype(int listtype) {
        this.listtype = listtype;
    }

    public int getLatecount() {
        return latecount;
    }

    public void setLatecount(int latecount) {
        this.latecount = latecount;
    }

    public int getEarlycount() {
        return earlycount;
    }

    public void setEarlycount(int earlycount) {
        this.earlycount = earlycount;
    }

    public ArrayList<DayofAttendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(ArrayList<DayofAttendance> attendances) {
        this.attendances = attendances;
    }

}
