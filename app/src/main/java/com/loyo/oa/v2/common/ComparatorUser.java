package com.loyo.oa.v2.common;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.other.model.User;

import java.util.Comparator;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class ComparatorUser implements Comparator {

    @Override
    public int compare(Object lhs, Object rhs) {
        User user0 = (User) lhs;
        User user1 = (User) rhs;

        if (TextUtils.isEmpty(user0.fullPinyin) || TextUtils.isEmpty(user1.fullPinyin)) {
            return 0;
        }

        return user0.fullPinyin.compareTo(user1.fullPinyin);
    }
}
