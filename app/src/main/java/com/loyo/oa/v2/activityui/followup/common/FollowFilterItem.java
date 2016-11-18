package com.loyo.oa.v2.activityui.followup.common;

import java.io.Serializable;

/**
 * Created by xeq on 16/11/18.
 */

public class FollowFilterItem implements Serializable {
    public String name;
    public String value;

    public FollowFilterItem(String name, String value) {
        this.name = name;
        this.value =value;
    }
}
