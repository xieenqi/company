package com.loyo.oa.v2.common;


import com.loyo.oa.v2.activityui.other.model.User;

import java.util.Comparator;

public class PinyinComparator implements Comparator<User> {

    public int compare(User o1, User o2) {
        if ("@".equals(o1.getSortLetters())
                || "#".equals(o2.getSortLetters())) {
            return -1;
        } else if ("#".equals(o1.getSortLetters())
                || "@".equals(o2.getSortLetters())) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
