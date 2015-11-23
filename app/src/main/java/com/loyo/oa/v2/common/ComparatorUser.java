package com.loyo.oa.v2.common;

import android.text.TextUtils;

import com.loyo.oa.v2.beans.User;

import java.util.Comparator;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class ComparatorUser implements Comparator {

    @Override
    public int compare(Object lhs, Object rhs) {
        User user0 = (User) lhs;
        User user1 = (User) rhs;

        if (TextUtils.isEmpty(user0.getFullPinyin()) || TextUtils.isEmpty(user1.getFullPinyin())) {
            return 0;
        }

        return user0.getFullPinyin().compareTo(user1.getFullPinyin());
    }
}
