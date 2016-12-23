package com.loyo.oa.v2.network.model;

import java.io.Serializable;

/**
 * Created by EthanGong on 2016/12/15.
 */

public class BaseResponse<T> implements Serializable {
    public int errcode;
    public String errmsg;
    public T data;

    public boolean isSuccess() {
        return errcode == 0;
    }
}
