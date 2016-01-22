package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/1/20.
 */
public class Suites implements Serializable{


    public String id;
    public String name;
    public String code;
    public boolean enable;
    private ArrayList<Modules> modules = new ArrayList<>();


    public ArrayList<Modules> getModules() {
        return modules;
    }

    public void setModules(ArrayList<Modules> modules) {
        this.modules = modules;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}