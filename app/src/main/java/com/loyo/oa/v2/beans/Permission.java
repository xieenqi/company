package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/1/20.
 */

public class Permission implements Serializable {

    public String name;

    public String code;

    public boolean enable = false;
    public int dataRange = 10;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
