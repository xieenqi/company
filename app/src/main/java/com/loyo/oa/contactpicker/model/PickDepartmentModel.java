package com.loyo.oa.contactpicker.model;

import android.support.annotation.NonNull;

import com.loyo.oa.indexablelist.widget.Indexable;
import com.loyo.oa.v2.db.bean.DBDepartment;

/**
 * Created by EthanGong on 2016/10/14.
 */

public class PickDepartmentModel implements Indexable {

    public final DBDepartment department;
    public boolean isLevel1;

    private PickDepartmentModel(@NonNull DBDepartment department) {
        this.department = department;
        isLevel1 = this.department.xpath.split("/").length <= 2;
    }

    public static PickDepartmentModel instance(@NonNull DBDepartment department) {
        return new PickDepartmentModel(department);
    }

    public String getName() {
        return department.name;
    }

    public boolean isLevel1() {
        return isLevel1;
    }

    @Override
    public String getIndex() {
        return department.getSortLetter();
    }
}
