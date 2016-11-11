package com.loyo.oa.v2.activityui.clue;

/**
 * Created by xeq on 16/11/11.
 */

public enum ClueTypeEnum {
    myCule(1), teamCule(2);
    private int type;

    ClueTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
