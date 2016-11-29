package com.loyo.oa.v2.permission;

import android.support.annotation.IntDef;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by loyo_dev1 on 16/1/20.
 */

// Updated by Ethan on 2016-11-29
//        DATA_RANGE_ZERO = 0 //无意义的数据范围,如公告通知
//        DATA_RANGE_ALL  = 1 // 公司可见
//        DATA_RANGE_DEPT = 2 // 部门可见
//        DATA_RANGE_SELF = 3 // 自己可见

@DatabaseTable(tableName = "permission")
public class Permission implements Serializable {

    public final static int ZERO     = 0;
    public final static int COMPANY  = 1;
    public final static int TEAM     = 2;
    public final static int PERSONAL = 3;
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

    @IntDef({ZERO, COMPANY, TEAM, PERSONAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataRange {
    }

}
