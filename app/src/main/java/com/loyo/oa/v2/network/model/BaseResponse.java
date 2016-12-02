package com.loyo.oa.v2.network.model;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {
    public int errcode;
    public String errmsg;
    public T data;
}
