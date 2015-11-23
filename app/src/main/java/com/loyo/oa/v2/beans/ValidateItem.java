package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :考勤打卡结果信息
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class ValidateItem implements Serializable {

    public static final int ATTENDANCE_STATE_IN = 1;
    public static final int ATTENDANCE_STATE_OUT = ATTENDANCE_STATE_IN + 1;

    private boolean enable;// (bool, optional): ,
    private boolean ischecked;// (bool, optional): ,
    private String reason;// (string, optional): ,
    private int type;// (int, optional):
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean ischecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
