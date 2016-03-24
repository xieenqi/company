package com.loyo.oa.v2.tool;

import java.util.ArrayList;

/**
 * Created by pj on 15/6/16.
 */
public final class ListUtil {
    protected ListUtil() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    public static boolean IsEmpty(ArrayList<?> list) {
        return (list == null || list.size() == 0);
    }
}
