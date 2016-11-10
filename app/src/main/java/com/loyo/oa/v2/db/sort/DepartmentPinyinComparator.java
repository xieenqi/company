package com.loyo.oa.v2.db.sort;

import com.loyo.oa.v2.db.bean.DBDepartment;

import java.util.Comparator;

/**
 * Created by EthanGong on 2016/10/12.
 */

public class DepartmentPinyinComparator implements Comparator<DBDepartment> {

    @Override
    public int compare(DBDepartment lhs, DBDepartment rhs) {
        if ("@".equals(lhs.getSortLetter())
                || "#".equals(rhs.getSortLetter())) {
            return -1;
        } else if ("#".equals(lhs.getSortLetter())
                || "@".equals(rhs.getSortLetter())) {
            return 1;
        } else {
            return lhs.pinyin().compareTo(rhs.pinyin());
        }
    }

}
