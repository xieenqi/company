package com.loyo.oa.v2.activityui.clue.common;

/**
 * Created by xeq on 16/11/11.
 */

public enum ClueType {
    MY_CLUE(1), TEAM_CLUE(2);
    private int type;

    ClueType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
