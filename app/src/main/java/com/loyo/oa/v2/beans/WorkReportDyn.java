package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by loyo_dev1 on 16/1/21.
 */
public class WorkReportDyn implements Serializable{

    private String name;
    private String num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
