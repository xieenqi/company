package com.loyo.oa.v2.network.model;

/**
 * Created by EthanGong on 2016/12/15.
 */

public class APIException extends Exception {
    public int code;
    public String message;
    public Object response;

    public APIException(int code, String message, Object response) {
        this.code = code;
        this.message = message;
        this.response = response;
    }

    @Override
    public String getMessage() {
        return message;
    }
}