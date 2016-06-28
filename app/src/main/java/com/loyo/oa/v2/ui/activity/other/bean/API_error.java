package com.loyo.oa.v2.ui.activity.other.bean;

/**
 * Created by lxf on 2015/1/7.
 */
public class API_error {
    private String error;

    public String getError() {
        return error==null?"":error;
    }

    public void setError(String error) {
        this.error = error;
    }
}