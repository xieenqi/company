package com.loyo.oa.v2.db.sort;

import com.loyo.oa.v2.db.bean.DBUser;

import java.util.Comparator;

/**
 * Created by EthanGong on 2016/10/12.
 */

public class UserIDComparator implements Comparator<DBUser> {

    @Override
    public int compare(DBUser lhs, DBUser rhs) {
        return lhs.id.compareTo(rhs.id);
    }
}
