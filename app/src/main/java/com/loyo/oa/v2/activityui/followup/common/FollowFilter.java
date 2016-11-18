package com.loyo.oa.v2.activityui.followup.common;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xeq on 16/11/18.
 */

public class FollowFilter implements Serializable {
    public String name;
    public String field;
    public ArrayList<FollowFilterItem> items;
}
