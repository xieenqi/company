package com.loyo.oa.v2.common;

import com.loyo.oa.v2.activityui.customer.model.Department;
import java.util.Comparator;

/**
 * com.loyo.oa.v2.common
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ComparatorContactGroup implements Comparator
{
    @Override
    public int compare(Object lhs, Object rhs) {
        Department department0=(Department)lhs;
        Department department1=(Department)rhs;
        return department0.getFullPinyin().compareTo(department1.getFullPinyin());
    }
}
