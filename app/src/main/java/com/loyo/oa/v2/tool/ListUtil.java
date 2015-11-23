package com.loyo.oa.v2.tool;

import java.util.ArrayList;

/**
 * Created by pj on 15/6/16.
 */
public final class ListUtil {
    public static boolean IsEmpty(ArrayList<?> list) {
        return (list == null || list.size() == 0);
    }
}
