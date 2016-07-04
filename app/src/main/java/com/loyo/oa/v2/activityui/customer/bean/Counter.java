package com.loyo.oa.v2.activityui.customer.bean;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/10/7.
 */
public class Counter implements Serializable {
    private int demand;
    private int task;
    private int visit;
    private int file;

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }
    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }
}
