package com.loyo.oa.v2.db.sort;

import com.loyo.oa.v2.db.bean.DBUser;

import java.util.Comparator;

/**
 * Created by EthanGong on 2016/10/12.
 */

public class UserPinyinComparator implements Comparator<DBUser> {

    @Override
    public int compare(DBUser lhs, DBUser rhs) {
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
