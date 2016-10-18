package com.loyo.oa.v2.db.sort;

import com.loyo.oa.v2.db.bean.DBDepartment;

import java.util.Comparator;

/**
 * Created by EthanGong on 2016/10/12.
 */

public class DepartmentIDComparator implements Comparator<DBDepartment> {

    @Override
    public int compare(DBDepartment lhs, DBDepartment rhs) {
        return lhs.id.compareTo(rhs.id);
    }
}
