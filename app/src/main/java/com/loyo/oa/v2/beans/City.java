package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :地区-市
 * 作者 : ykb
 * 时间 : 15/10/19.
 */
public class City extends BaseMultiMenuItem {
    private ArrayList<County> counties=new ArrayList<>();

    public ArrayList<County> getCounties() {
        return counties;
    }

    public void setCounties(ArrayList<County> counties) {
        this.counties = counties;
    }

    @Override
    protected boolean hasChildren() {
        return true;
    }
    @Override
    public ArrayList<County> getChildren() {
        return counties;
    }
}
