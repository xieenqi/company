package com.loyo.oa.v2.beans;

import com.loyo.oa.v2.activityui.customer.bean.City;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :地区-省
 * 作者 : ykb
 * 时间 : 15/10/19.
 */
public class Province extends BaseMultiMenuItem {
    private ArrayList<City> cities=new ArrayList<>();

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    @Override
    protected boolean hasChildren() {
        return true;
    }

    @Override
    public ArrayList<City> getChildren() {
        return cities;
    }
}
