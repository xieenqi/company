package com.loyo.oa.v2.common;

import com.loyo.oa.v2.activityui.other.model.UserGroupData;

import java.util.Comparator;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class ComparatorUserGroupData implements Comparator {

    @Override
    public int compare(Object lhs, Object rhs) {
        UserGroupData userGroupData0=(UserGroupData)lhs;
        UserGroupData userGroupData1=(UserGroupData)rhs;
//        int flag=user0.getFullPinyin().compareTo(user1.getFullPinyin());
        return userGroupData0.getGroupName().compareTo(userGroupData1.getGroupName());
    }
}
