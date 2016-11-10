package com.loyo.oa.voip;

/**
 * Created by EthanGong on 2016/11/2.
 */

public enum  DTMF {

    ZERO(7,   "0"),
    ONE(8,    "1"),
    TWO(9,    "2"),
    THREE(10, "3"),
    FOUR(11,  "4"),
    FIVE(12,  "5"),
    SIX(13,   "6"),
    SEVEN(14, "7"),
    EIGHT(15, "8"),
    NINE(16,  "9"),
    STAR(17,  "*"),
    HASH(18,  "#");



    private int code;
    private String key;

    DTMF(int code, String key) {
        this.code = code;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public int getCode() {
        return code;
    }
}
