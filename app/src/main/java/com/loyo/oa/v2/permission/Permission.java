package com.loyo.oa.v2.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/1/20.
 */
@DatabaseTable(tableName = "permission")
public class Permission implements Serializable {

    public static int COMPANY = 1;
    public static int TEAM = 2;
    public static int PERSONAL = 3;
    @DatabaseField
    public String name;
    @DatabaseField
    public String code;
    @DatabaseField
    public boolean enable = false;
    @DatabaseField
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
